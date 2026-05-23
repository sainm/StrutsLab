#!/usr/bin/env python3
"""XLSX generator with styles using only stdlib (xml + zipfile)."""

import zipfile
import xml.etree.ElementTree as ET
import os
import re

NS = 'http://schemas.openxmlformats.org/spreadsheetml/2006/main'
NS_R = 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'

ET.register_namespace('', NS)
ET.register_namespace('r', NS_R)

def qn(tag):
    return f'{{{NS}}}{tag}'

def qnr(tag):
    return f'{{{NS_R}}}{tag}'

# ============ Styles ============

STYLE_HEADER = 0     # Bold, light blue bg, border
STYLE_NORMAL = 1     # Normal, border
STYLE_TITLE = 2      # Bold large, light blue bg, border, merged
STYLE_DOCINFO_K = 3  # Bold, light gray bg, border
STYLE_DOCINFO_V = 4  # Normal, border
STYLE_SECTION = 5    # Bold, light green bg, border

def _make_styles_xml():
    """Generate styles.xml with fonts, fills, borders, cellXfs."""
    el = ET.Element('styleSheet')

    # Fonts (index)
    fonts = ET.SubElement(el, qn('fonts'), {'count': '3'})
    # Font 0: default
    et_fn0 = ET.SubElement(fonts, qn('font'))
    ET.SubElement(et_fn0, qn('sz'), {'val': '11'})
    ET.SubElement(et_fn0, qn('name'), {'val': 'MS PGothic'})
    # Font 1: bold
    et_fn1 = ET.SubElement(fonts, qn('font'))
    ET.SubElement(et_fn1, qn('b'))
    ET.SubElement(et_fn1, qn('sz'), {'val': '11'})
    ET.SubElement(et_fn1, qn('name'), {'val': 'MS PGothic'})
    # Font 2: bold large
    et_fn2 = ET.SubElement(fonts, qn('font'))
    ET.SubElement(et_fn2, qn('b'))
    ET.SubElement(et_fn2, qn('sz'), {'val': '14'})
    ET.SubElement(et_fn2, qn('name'), {'val': 'MS PGothic'})

    # Fills (index)
    fills = ET.SubElement(el, qn('fills'), {'count': '5'})
    # Fill 0: none
    f0 = ET.SubElement(fills, qn('fill'))
    pf0 = ET.SubElement(f0, qn('patternFill'), {'patternType': 'none'})
    # Fill 1: gray125
    f1 = ET.SubElement(fills, qn('fill'))
    pf1 = ET.SubElement(f1, qn('patternFill'), {'patternType': 'gray125'})
    # Fill 2: light blue bg
    f2 = ET.SubElement(fills, qn('fill'))
    pf2 = ET.SubElement(f2, qn('patternFill'), {'patternType': 'solid'})
    ET.SubElement(pf2, qn('fgColor'), {'rgb': '00DDDDEE'})
    ET.SubElement(pf2, qn('bgColor'), {'indexed': '64'})
    # Fill 3: light gray bg
    f3 = ET.SubElement(fills, qn('fill'))
    pf3 = ET.SubElement(f3, qn('patternFill'), {'patternType': 'solid'})
    ET.SubElement(pf3, qn('fgColor'), {'rgb': '00F4F4F4'})
    ET.SubElement(pf3, qn('bgColor'), {'indexed': '64'})
    # Fill 4: light green bg
    f4 = ET.SubElement(fills, qn('fill'))
    pf4 = ET.SubElement(f4, qn('patternFill'), {'patternType': 'solid'})
    ET.SubElement(pf4, qn('fgColor'), {'rgb': '00DDFFDD'})
    ET.SubElement(pf4, qn('bgColor'), {'indexed': '64'})

    # Borders (index)
    borders = ET.SubElement(el, qn('borders'), {'count': '3'})
    # Border 0: none
    ET.SubElement(borders, qn('border'))
    # Border 1: thin all around
    thin_border = ET.SubElement(borders, qn('border'))
    for pos in ['left', 'right', 'top', 'bottom']:
        ET.SubElement(thin_border, qn(pos), {'style': 'thin'})
    # Border 2: thin bottom only
    bottom_border = ET.SubElement(borders, qn('border'))
    ET.SubElement(bottom_border, qn('bottom'), {'style': 'thin'})

    # Cell Style Xfs (index → fontId, fillId, borderId)
    cell_xfs = ET.SubElement(el, qn('cellXfs'), {'count': '6'})
    # 0: Header = bold + blue bg + border
    ET.SubElement(cell_xfs, qn('xf'), {
        'fontId': '1', 'fillId': '2', 'borderId': '1',
        'applyFont': '1', 'applyFill': '1', 'applyBorder': '1'
    })
    # 1: Normal = default + border
    ET.SubElement(cell_xfs, qn('xf'), {
        'fontId': '0', 'fillId': '0', 'borderId': '1',
        'applyBorder': '1'
    })
    # 2: Title = bold large + blue bg + border
    ET.SubElement(cell_xfs, qn('xf'), {
        'fontId': '2', 'fillId': '2', 'borderId': '1',
        'applyFont': '1', 'applyFill': '1', 'applyBorder': '1'
    })
    # 3: DocInfo Key = bold + gray bg + border
    ET.SubElement(cell_xfs, qn('xf'), {
        'fontId': '1', 'fillId': '3', 'borderId': '1',
        'applyFont': '1', 'applyFill': '1', 'applyBorder': '1'
    })
    # 4: Section = bold + green bg + border
    ET.SubElement(cell_xfs, qn('xf'), {
        'fontId': '1', 'fillId': '4', 'borderId': '1',
        'applyFont': '1', 'applyFill': '1', 'applyBorder': '1'
    })
    # 5: bold + border (no bg)
    ET.SubElement(cell_xfs, qn('xf'), {
        'fontId': '1', 'fillId': '0', 'borderId': '1',
        'applyFont': '1', 'applyBorder': '1'
    })

    return ET.tostring(el, encoding='unicode', xml_declaration=True)


class XlsxWriter:
    def __init__(self):
        self.sheets = {}     # name -> list of list of (str, style_id)
        self.col_widths = {} # name -> list of int
        self.merged = {}     # name -> list of str like "A1:B2"
        self._strings = []   # shared strings
        self._str_idx = {}   # string -> index

    def _get_str_idx(self, s):
        s = str(s) if s else ''
        if s not in self._str_idx:
            self._str_idx[s] = len(self._strings)
            self._strings.append(s)
        return self._str_idx[s]

    def add_sheet(self, name, data, col_widths=None, merged=None):
        """data is list of list of str or list of list of (str, style_id)."""
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
        el = ET.Element(qn('worksheet'))

        sv = ET.SubElement(el, qn('sheetViews'))
        ET.SubElement(sv, qn('sheetView'), {'tabSelected': '1', 'workbookViewId': '0'})

        ET.SubElement(el, qn('sheetFormatPr'), {'defaultRowHeight': '18'})

        # cols
        if name in self.col_widths:
            cols_el = ET.SubElement(el, qn('cols'))
            for i, w in enumerate(self.col_widths[name], 1):
                ET.SubElement(cols_el, qn('col'), {
                    'min': str(i), 'max': str(i), 'width': str(w), 'customWidth': '1'
                })

        sheet_data = ET.SubElement(el, qn('sheetData'))
        for ri, row_data in enumerate(rows, 1):
            row_el = ET.SubElement(sheet_data, qn('row'), {'r': str(ri), 'ht': '20'})
            for ci, cell_data in enumerate(row_data):
                if isinstance(cell_data, tuple):
                    text, style = cell_data
                else:
                    text, style = cell_data, 1  # default: normal+border

                col_letter = self._col_letter(ci)
                ref = f'{col_letter}{ri}'
                idx = self._get_str_idx(text)
                c_el = ET.SubElement(row_el, qn('c'), {
                    'r': ref, 't': 's', 's': str(style)
                })
                v_el = ET.SubElement(c_el, qn('v'))
                v_el.text = str(idx)

        # Merged cells
        if name in self.merged and self.merged[name]:
            mc_el = ET.SubElement(el, qn('mergeCells'), {
                'count': str(len(self.merged[name]))
            })
            for m in self.merged[name]:
                ET.SubElement(mc_el, qn('mergeCell'), {'ref': m})

        ET.SubElement(el, qn('pageMargins'), {
            'left': '0.7', 'right': '0.7', 'top': '0.75',
            'bottom': '0.75', 'header': '0.3', 'footer': '0.3'
        })

        return ET.tostring(el, encoding='unicode', xml_declaration=True)

    def _make_shared_strings_xml(self):
        el = ET.Element(qn('sst'), {
            'count': str(len(self._strings)),
            'uniqueCount': str(len(self._strings))
        })
        for s in self._strings:
            si = ET.SubElement(el, qn('si'))
            t = ET.SubElement(si, qn('t'))
            t.text = self._escape(s)
        return ET.tostring(el, encoding='unicode', xml_declaration=True)

    def save(self, path):
        """Write xlsx file."""
        with zipfile.ZipFile(path, 'w', zipfile.ZIP_DEFLATED) as zf:
            # [Content_Types].xml
            ct = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            ct.append('<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">')
            ct.append('<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>')
            ct.append('<Default Extension="xml" ContentType="application/xml"/>')
            ct.append('<Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>')
            ct.append('<Override PartName="/xl/sharedStrings.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"/>')
            for i in range(len(self.sheets)):
                ct.append(f'<Override PartName="/xl/worksheets/sheet{i+1}.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>')
            ct.append('<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>')
            ct.append('</Types>')
            zf.writestr('[Content_Types].xml', '\n'.join(ct))

            # _rels/.rels
            rels = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            rels.append('<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">')
            rels.append('<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>')
            rels.append('</Relationships>')
            zf.writestr('_rels/.rels', '\n'.join(rels))

            # xl/styles.xml
            zf.writestr('xl/styles.xml', _make_styles_xml())

            # xl/sharedStrings.xml
            zf.writestr('xl/sharedStrings.xml', self._make_shared_strings_xml())

            # xl/workbook.xml
            wb = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            wb.append(f'<workbook xmlns="{NS}" xmlns:r="{NS_R}">')
            wb.append('<sheets>')
            for i, name in enumerate(self.sheets.keys(), 1):
                wb.append(f'<sheet name="{self._escape(name)}" sheetId="{i}" r:id="rId{i}"/>')
            wb.append('</sheets>')
            wb.append('</workbook>')
            zf.writestr('xl/workbook.xml', '\n'.join(wb))

            # xl/_rels/workbook.xml.rels
            wb_rels = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>']
            wb_rels.append('<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">')
            for i in range(len(self.sheets)):
                wb_rels.append(f'<Relationship Id="rId{i+1}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet{i+1}.xml"/>')
            wb_rels.append('<Relationship Id="rIdStyles" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>')
            wb_rels.append('<Relationship Id="rIdSharedStrings" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings" Target="sharedStrings.xml"/>')
            wb_rels.append('</Relationships>')
            zf.writestr('xl/_rels/workbook.xml.rels', '\n'.join(wb_rels))

            # Worksheets
            for i, name in enumerate(self.sheets.keys(), 1):
                xml = self._make_sheet_xml(name, self.sheets[name])
                zf.writestr(f'xl/worksheets/sheet{i}.xml', xml)

        print(f'  -> {os.path.basename(path)}')


# ============ Helpers ============

def H(items):
    """Header row: bold + blue bg + border."""
    return [(str(x), STYLE_HEADER) for x in items]

def R(items):
    """Normal data row: border."""
    return [(str(x), STYLE_NORMAL) for x in items]

def K(items):
    """Key column (bold gray bg) + value columns."""
    return [(str(items[0]), STYLE_DOCINFO_K)] + [(str(x), STYLE_NORMAL) for x in items[1:]]

def T(text, cols=8):
    """Title row: bold large + blue bg, merged across cols."""
    a = 'A'
    b = chr(ord('A') + cols - 1)
    return [(text, STYLE_TITLE)], f'{a}1:{b}1'

def S(text, cols=8):
    """Section header: bold + green bg."""
    return [(text, STYLE_SECTION)] + [('', STYLE_SECTION)] * (cols - 1)

def E(n=8):
    """Empty row."""
    return [('', STYLE_NORMAL)] * n

def doc_info_8():
    """Standard 8-column doc info."""
    return [
        R(['文書管理情報', '', '', '', '', '', '', '']),
        K(['システムID', 'STRUTSLAB-001', '', '', 'システム名', '電力設備巡視点検管理システム', '', '']),
        K(['サブシステム名', '', '', '', '開発言語', 'Java 1.8 / Struts1 / MyBatis', '', '']),
        K(['データベース', 'H2 Database（ファイルモード）', '', '', '文書番号', '', '', '']),
        K(['版数', '1.0', '', '', '作成日', '2026/05/23', '', '']),
        K(['作成者', 'システム開発部', '', '', '承認者', '', '', '']),
    ]

def doc_info_6():
    """Standard 6-column doc info."""
    return [
        R(['文書管理情報', '', '', '', '', '']),
        K(['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', '']),
        K(['画面ID', '', '', '', '画面名', '']),
        K(['プログラムID', '', '', '', '文書番号', '']),
        K(['版数', '1.0', '', '', '作成日', '2026/05/23']),
        K(['作成者', 'システム開発部', '', '', '区分', '新規']),
    ]

REVISION_HISTORY = [
    H(['版数', '改訂日', '改訂内容', '改訂者', '承認者', '区分', '備考']),
    R(['1.0', '2026/05/23', '初版作成', 'システム開発部', '', '新規', '']),
]


def make_standard_workbook(main_sheet_name, main_data, **extra_sheets):
    """Standard 3-sheet: revision history + empty + main data."""
    w = XlsxWriter()
    w.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5, 12, 30, 15, 15, 8, 20])
    w.add_sheet('Sheet2', [])
    w.add_sheet(main_sheet_name, main_data)
    for sname, sdata in extra_sheets.items():
        w.add_sheet(sname, sdata)
    return w
