Option Explicit
 
Const vbHide = 0             '�E�B���h�E���\��
Const vbNormalFocus = 1      '�ʏ�̃E�B���h�E�A���őO�ʂ̃E�B���h�E
Const vbMinimizedFocus = 2   '�ŏ����A���őO�ʂ̃E�B���h�E
Const vbMaximizedFocus = 3   '�ő剻�A���őO�ʂ̃E�B���h�E
Const vbNormalNoFocus = 4    '�ʏ�̃E�B���h�E�A�������A�őO�ʂɂ͂Ȃ�Ȃ�
Const vbMinimizedNoFocus = 6 '�ŏ����A�������A�őO�ʂɂ͂Ȃ�Ȃ�
 
Dim objWShell
Set objWShell = CreateObject("WScript.Shell")
objWShell.Run "cmd /c StartUp.bat", vbHide, False
 
Set objWShell = Nothing
