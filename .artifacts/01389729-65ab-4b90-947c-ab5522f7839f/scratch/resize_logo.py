from PIL import Image
import os

logo_path = "app_logo.png"
img = Image.open(logo_path).convert("RGBA")

densities = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192
}

res_dir = "app/src/main/res"

for density, size in densities.items():
    folder = os.path.join(res_dir, density)
    os.makedirs(folder, exist_ok=True)

    resized = img.resize((size, size), Image.Resampling.LANCZOS)

    resized.save(os.path.join(folder, "ic_launcher.png"), "PNG")
    resized.save(os.path.join(folder, "ic_launcher_round.png"), "PNG")

print("Successfully generated all launcher icons from app_logo.png!")
