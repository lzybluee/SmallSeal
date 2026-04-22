python -m pip list | find "matplotlib"
if %errorlevel% neq 0 (
    python -m pip install matplotlib
)

python -m pip list | find "pyperclip"
if %errorlevel% neq 0 (
    python -m pip install pyperclip
)

python -m pip list | find "pillow"
if %errorlevel% neq 0 (
    python -m pip install pillow
)

python main.py