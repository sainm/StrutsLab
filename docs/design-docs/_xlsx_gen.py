#!/usr/bin/env python3
"""XLSX generator using only stdlib (xml + zipfile)."""

import zipfile
import xml.etree.ElementTree as ET
import io
import os
import re

NS = 'http://schemas.openxmlformats.org/spreadsheetml/2006/main'
NS_R = 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'
NS_MC = 'http://schemas.openxmlformats.org/officeDocument/2006/math'
NS_X14 = 'http://schemas.microsoft.com/office/spreadsheetml/2009/9/main'

ET.register_namespace('', NS)
ET.register_namespace('r', NS_R)
ET.register_namespace('mc', NS_MC)
ET.register_namespace('x14', NS_X14)

def qn(tag):
    return f'{{{NS}}}{tag}'

def qnr(tag):
    return f'{{{NS_R}}}{tag}'

class XlsxWriter:
    def __init__(self):
        self.sheets = {}  # name -> list of list of str
        self.col_widths = {}  # name -> list of int (optional)
        self.merged = {}  # name -> list of str like "A1:B2"

    def add_sheet(self, name, data, col_widths=None, merged=None):
        """data is list of list of str."""
        self.sheets[name] = data
        if col_widths:
            self.col_widths[name] = col_widths
        if merged:
            self.merged[name] = merged

    def _col_letter(self, n):
        s = ''
        while n >= 0:
            s = chr(ord('A') + n % 26) + s
            n = n // 26 - 1
        return s

    def _escape(self, s):
        return str(s).replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('"', '&quot;')

    def _make_sheet_xml(self, name, rows):
        el = ET.Element(qn('worksheet'), {
            'xmlns:mc': NS_MC,
            'xmlns:x14ac': 'http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac',
        })

        sv = ET.SubElement(el, qn('sheetViews'))
        sv2 = ET.SubElement(sv, qn('sheetView'), {'tabSelected': '1', 'workbookViewId': '0'})

        fmt = ET.SubElement(el, qn('sheetFormatPr'), {'defaultRowHeight': '15'})

        # cols
        if name in self.col_widths:
            cols_el = ET.SubElement(el, qn('cols'))
            for i, w in enumerate(self.col_widths[name], 1):
                ET.SubElement(cols_el, qn('col'), {'min': str(i), 'max': str(i), 'width': str(w), 'customWidth': '1'})

        sheet_data = ET.SubElement(el, qn('sheetData'))
        for ri, row in enumerate(rows, 1):
            row_el = ET.SubElement(sheet_data, qn('row'), {'r': str(ri)})
            for ci, val in enumerate(row):
                col_letter = self._col_letter(ci)
                ref = f'{col_letter}{ri}'
                c_el = ET.SubElement(row_el, qn('c'), {'r': ref, 't': 'inlineStr'})
                is_el = ET.SubElement(c_el, qn('is'))
                t_el = ET.SubElement(is_el, qn('t'))
                t_el.text = self._escape(val) if val else ''

        # merged cells
        if name in self.merged and self.merged[name]:
            mc_el = ET.SubElement(el, qn('mergeCells'), {'count': str(len(self.merged[name]))})
            for m in self.merged[name]:
                ET.SubElement(mc_el, qn('mergeCell'), {'ref': m})

        page = ET.SubElement(el, qn('pageMargins'), {
            'left': '0.7', 'right': '0.7', 'top': '0.75', 'bottom': '0.75', 'header': '0.3', 'footer': '0.3'
        })

        return ET.tostring(el, encoding='unicode', xml_declaration=True)

    def save(self, path):
        """Write xlsx file to path."""
        with zipfile.ZipFile(path, 'w', zipfile.ZIP_DEFLATED) as zf:
            # [Content_Types].xml
            ct_types = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            ct_types.append('<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">')
            ct_types.append('<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>')
            ct_types.append('<Default Extension="xml" ContentType="application/xml"/>')
            for i in range(len(self.sheets)):
                ct_types.append(f'<Override PartName="/xl/worksheets/sheet{i+1}.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>')
            ct_types.append('<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>')
            ct_types.append('</Types>')
            zf.writestr('[Content_Types].xml', '\n'.join(ct_types))

            # _rels/.rels
            rels = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            rels.append('<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">')
            rels.append('<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>')
            rels.append('</Relationships>')
            zf.writestr('_rels/.rels', '\n'.join(rels))

            # xl/workbook.xml
            wb = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            wb.append('<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">')
            wb.append('<sheets>')
            sheet_names = list(self.sheets.keys())
            for i, name in enumerate(sheet_names, 1):
                wb.append(f'<sheet name="{self._escape(name)}" sheetId="{i}" r:id="rId{i}"/>')
            wb.append('</sheets>')
            wb.append('</workbook>')
            zf.writestr('xl/workbook.xml', '\n'.join(wb))

            # xl/_rels/workbook.xml.rels
            wb_rels = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            wb_rels.append('<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">')
            for i in range(len(sheet_names)):
                wb_rels.append(f'<Relationship Id="rId{i+1}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet{i+1}.xml"/>')
            wb_rels.append('</Relationships>')
            zf.writestr('xl/_rels/workbook.xml.rels', '\n'.join(wb_rels))

            # worksheets
            for i, name in enumerate(sheet_names, 1):
                xml = self._make_sheet_xml(name, self.sheets[name])
                zf.writestr(f'xl/worksheets/sheet{i}.xml', xml)

        print(f'  -> {os.path.basename(path)}')


def hdr_row(items):
    """Generate header row data (bold labels)."""
    return [str(x) for x in items]

def data_rows(rows):
    return [[str(c) for c in r] for r in rows]

def empty_row(n=8):
    return [''] * n

DOC_INFO_8 = [
    ['システムID', 'STRUTSLAB-001', '', '', 'システム名', '電力設備巡視点検管理システム', '', ''],
    ['サブシステム名', '設備巡視点検', '', '', '開発言語', 'Java 1.8 / Struts1 / MyBatis', '', ''],
    ['データベース', 'H2 Database（ファイルモード）', '', '', '文書番号', '', '', ''],
    ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
    ['作成者', 'システム開発部', '', '', '承認者', '', '', ''],
]

DOC_INFO_6 = [
    ['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', ''],
    ['画面ID', '', '', '', '画面名', ''],
    ['プログラムID', '', '', '', '文書番号', ''],
    ['版数', '1.0', '', '', '作成日', '2026/05/23', ''],
    ['作成者', 'システム開発部', '', '', '区分', '新規', ''],
]

REVISION_HISTORY = [
    hdr_row(['版数', '改訂日', '改訂内容', '改訂者', '承認者', '区分', '備考']),
    ['1.0', '2026/05/23', '初版作成', 'システム開発部', '', '新規', ''],
]


def make_standard_workbook(main_sheet_name, main_data, **extra_sheets):
    """Create a standard 3-sheet workbook: revision history + empty + main data."""
    w = XlsxWriter()
    w.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5, 12, 30, 15, 15, 8, 20])
    w.add_sheet('Sheet2', [])
    w.add_sheet(main_sheet_name, main_data)
    for sname, sdata in extra_sheets.items():
        w.add_sheet(sname, sdata)
    return w
