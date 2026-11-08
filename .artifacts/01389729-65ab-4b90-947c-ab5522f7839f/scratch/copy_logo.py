import shutil
import os

os.makedirs("app/src/main/res/drawable", exist_ok=True)
shutil.copy("app_logo.png", "app/src/main/res/drawable/app_logo.png")
print("Copied app_logo.png to drawable!")
