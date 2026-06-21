import docx
import sys

files = [
    'ПИЖ1_Джабраилов.docx',
    'ПИЖ2_Джабраилов.docx', 
    'ПИЖ3_Джабраилов.docx',
    'ПИЖ4_Джабраилов.docx',
    'ПИЖ5_Джабраилов.docx'
]

for f in files:
    print(f"\n{'='*60}")
    print(f"=== {f} ===")
    print(f"{'='*60}")
    try:
        doc = docx.Document(f)
        for p in doc.paragraphs:
            if p.text.strip():
                print(p.text)
    except Exception as e:
        print(f"Error: {e}")
