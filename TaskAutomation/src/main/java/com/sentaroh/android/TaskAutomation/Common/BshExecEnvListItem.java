package com.sentaroh.android.TaskAutomation.Common;

import com.sentaroh.android.TaskAutomation.BeanShellDriver;

import bsh.Interpreter;

public class BshExecEnvListItem {
	public boolean isUsed=false;
	public long driverId=0;
	public BeanShellDriver driver=null;
	public Interpreter interpreter=null;
}
