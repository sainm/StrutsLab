#!/usr/bin/env python3
"""
Generate all design-doc Excel files in OperaMind-compatible format.

Usage:
    python generate_design_docs.py           # generate all 16 files
    python generate_design_docs.py --clean   # delete old xlsx first, then generate

Output files follow OperaMind styling:
  - 游ゴシック (Yu Gothic) font throughout
  - Dark navy #1E293B table headers with white text
  - Light blue #DBEAFE section headers
  - Light gray #F1F5F9 label cells
  - Thin borders, wrap text, proper merged cells
  - 改訂履歷 (revision history) sheet in every workbook
"""

import sys
import os

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

if "--clean" in sys.argv:
    for f in os.listdir(HERE):
        if f.endswith(".xlsx"):
            os.remove(os.path.join(HERE, f))
    print("Cleaned old .xlsx files.\n")

# Run the three generation scripts
scripts = ["gen_all_styled.py", "gen_all_styled_2.py", "gen_all_styled_3.py"]
for s in scripts:
    path = os.path.join(HERE, s)
    with open(path) as src:
        exec(compile(src.read(), s, "exec"))

print("\nAll 16 design-doc Excel files generated.")
