#!/usr/bin/env python3
import json, glob, os, sys

DATA_DIR = os.path.join(os.path.dirname(__file__), "assets", "data")
OUTPUT_JS = os.path.join(os.path.dirname(__file__), "assets", "stories.js")
OUTPUT_JS_ROOT = os.path.join(os.path.dirname(__file__), "stories.js")

def main():
    files = sorted(glob.glob(os.path.join(DATA_DIR, "batch_*.json")))
    if not files:
        print("No batch files found in", DATA_DIR)
        sys.exit(1)

    all_stories = []
    seen_ids = set()

    for file_path in files:
        with open(file_path, "r", encoding="utf-8") as f:
            try:
                batch = json.load(f)
            except Exception as e:
                print(f"Error parsing {file_path}: {e}")
                sys.exit(1)

            for s in batch:
                sid = s.get("id")
                if not sid:
                    print(f"Missing id in story in {file_path}: {s.get('title')}")
                    sys.exit(1)
                if sid in seen_ids:
                    print(f"Duplicate id '{sid}' found in {file_path}")
                    sys.exit(1)
                seen_ids.add(sid)

                # Validate sentences
                sentences = s.get("sentences", [])
                if not sentences:
                    print(f"No sentences in story '{sid}' in {file_path}")
                    sys.exit(1)

                all_stories.append(s)

    # Write combined stories.js
    js_content = "// StoryLingo - Curated Massive Bilingual Stories Database\n\nconst STORIES_DATA = "
    js_content += json.dumps(all_stories, ensure_ascii=False, indent=2)
    js_content += ";\n\nif (typeof module !== 'undefined' && module.exports) {\n  module.exports = STORIES_DATA;\n}\n"

    with open(OUTPUT_JS, "w", encoding="utf-8") as f:
        f.write(js_content)
    with open(OUTPUT_JS_ROOT, "w", encoding="utf-8") as f:
        f.write(js_content)

    print(f"Successfully compiled {len(all_stories)} stories from {len(files)} batches into {OUTPUT_JS} and {OUTPUT_JS_ROOT}")

if __name__ == "__main__":
    main()
