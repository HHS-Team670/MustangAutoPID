$proc = Get-Process | Where-Object { $_.MainWindowTitle -like '*AdvantageScope*' -and $_.MainWindowHandle -ne 0 }
if ($proc) {
    $hwnd = $proc.MainWindowHandle

    Add-Type -TypeDefinition @"
using System;
using System.Runtime.InteropServices;
public class Win32 {
    [DllImport("user32.dll")]
    public static extern bool SetForegroundWindow(IntPtr hWnd);
    [DllImport("user32.dll")]
    public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
    [DllImport("user32.dll")]
    public static extern bool AllowSetForegroundWindow(IntPtr hWnd);
    [DllImport("user32.dll")]
    public static extern bool BringWindowToTop(IntPtr hWnd);
}
"@ -PassThru

    # Allow this process to set foreground
    [Win32]::AllowSetForegroundWindow([IntPtr]::Zero)

    if ([Win32]::IsIconic($hwnd)) {
        [Win32]::ShowWindow($hwnd, 9)  # SW_RESTORE
    }

    # Bring to front
    [Win32]::BringWindowToTop($hwnd)
    [Win32]::SetForegroundWindow($hwnd)
} else {
    Start-Process "C:\Users\Public\wpilib\2025\advantagescope\AdvantageScope (WPILib).exe"
}
