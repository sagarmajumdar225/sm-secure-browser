import os

res_dir = "app/src/main/res"
for root, dirs, files in os.walk(res_dir):
    for file in files:
        if file.endswith(".webp") and "ic_launcher" in file:
            path = os.path.join(root, file)
            os.remove(path)
            print(f"Removed duplicate webp: {path}")
