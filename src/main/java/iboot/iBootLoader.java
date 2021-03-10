package iboot;

import java.io.IOException;
import java.util.*;

import ghidra.app.util.Option;
import ghidra.app.util.bin.ByteProvider;
import ghidra.app.util.importer.MessageLog;
import ghidra.app.util.opinion.AbstractLibrarySupportLoader;
import ghidra.app.util.opinion.LoadSpec;
import ghidra.framework.model.DomainObject;
import ghidra.program.model.lang.LanguageCompilerSpecPair;
import ghidra.program.model.listing.Program;
import ghidra.util.exception.CancelledException;
import ghidra.util.exception.InvalidInputException;
import ghidra.util.task.TaskMonitor;

public class iBootLoader extends AbstractLibrarySupportLoader {
	@Override
	public String getName() {
		return "iBoot Stage";
	}

	@Override
	public Collection<LoadSpec> findSupportedLoadSpecs(ByteProvider provider) throws IOException {
		var result = new ArrayList<LoadSpec>();
		try {
			iBootInfo version = new iBootInfo(provider);
			if (version.isSupported()) {
				result.add(new LoadSpec(this, version.getBaseAddress(),
						new LanguageCompilerSpecPair("AARCH64:LE:64:v8A", "default"),
							true));
			}
		} catch (InvalidInputException exception) {

		}
		return result;
	}

	@Override
	protected void load(ByteProvider provider, LoadSpec loadSpec, List<Option> options,
						Program program, TaskMonitor monitor, MessageLog log) throws CancelledException, IOException {
	}

	@Override
	public List<Option> getDefaultOptions(ByteProvider provider, LoadSpec loadSpec, DomainObject domainObject,
										  boolean isLoadIntoProgram) {
		return super.getDefaultOptions(provider, loadSpec, domainObject, isLoadIntoProgram);
	}

	@Override
	public String validateOptions(ByteProvider provider, LoadSpec loadSpec, List<Option> options, Program program) {
		return super.validateOptions(provider, loadSpec, options, program);
	}
}
