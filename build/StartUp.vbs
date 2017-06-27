Option Explicit
 
Const vbHide = 0             'ウィンドウを非表示
Const vbNormalFocus = 1      '通常のウィンドウ、かつ最前面のウィンドウ
Const vbMinimizedFocus = 2   '最小化、かつ最前面のウィンドウ
Const vbMaximizedFocus = 3   '最大化、かつ最前面のウィンドウ
Const vbNormalNoFocus = 4    '通常のウィンドウ、ただし、最前面にはならない
Const vbMinimizedNoFocus = 6 '最小化、ただし、最前面にはならない

Dim cmdReturn
Dim objWShell
Set objWShell = CreateObject("WScript.Shell")

'管理画面を起動
cmdReturn = objWShell.Run("cmd /c MyColoring.bat --startup", vbHide, true)

'管理画面「保存して起動」の場合
If cmdReturn = 1 then
    '監視ソフトを起動
    objWShell.Run "%windir%\system32\notepad.exe", vbMinimizedNoFocus, false
    'ProcessingSketchを起動
    objWShell.Run "cmd /c MainSketch.bat", vbNormalNoFocus, false
end if

Set objWShell = Nothing
