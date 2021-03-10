package iboot;

import ghidra.app.services.AbstractAnalyzer;
import ghidra.app.services.AnalyzerType;
import ghidra.app.util.importer.MessageLog;
import ghidra.framework.options.Options;
import ghidra.program.model.address.AddressSetView;
import ghidra.program.model.listing.Program;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;

public class iBootAnalyzer extends AbstractAnalyzer {
	public iBootAnalyzer() {
		super("iBoot Stage Analyzer", "Attempts to symbolize iBoot binaries",
				AnalyzerType.FUNCTION_ANALYZER);
	}

	@Override
	public boolean getDefaultEnablement(Program program) {
		return false;
	}

	@Override
	public boolean canAnalyze(Program program) {
		return false;
	}

	@Override
	public void registerOptions(Options options, Program program) {
	}

	@Override
	public boolean added(Program program, AddressSetView set, TaskMonitor monitor, MessageLog log)
			throws CancelledException {
		return false;
	}
}
