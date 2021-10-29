package iboot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ghidra.app.util.Option;
import ghidra.app.util.bin.ByteProvider;
import ghidra.app.util.importer.MessageLog;
import ghidra.app.util.opinion.AbstractLibrarySupportLoader;
import ghidra.app.util.opinion.LoadSpec;
import ghidra.framework.model.DomainObject;
import ghidra.program.flatapi.FlatProgramAPI;
import ghidra.program.model.address.Address;
import ghidra.program.model.lang.LanguageCompilerSpecPair;
import ghidra.program.model.listing.Program;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.mem.MemoryBlock;
import ghidra.util.exception.CancelledException;
import ghidra.util.exception.InvalidInputException;
import ghidra.util.task.TaskMonitor;

public class iBootLoader extends AbstractLibrarySupportLoader {
	@Override
	public String getName() {
		return "iBoot Image";
	}

	@Override
	public Collection<LoadSpec> findSupportedLoadSpecs(ByteProvider provider) throws IOException {
		var result = new ArrayList<LoadSpec>();
		try {
			iBootInfo info = new iBootInfo(provider);
			if (info.is64Bit()) {
				result.add(new LoadSpec(this, info.getBaseAddress(),
						new LanguageCompilerSpecPair("AARCH64:LE:64:v8A", "default"),
						true));
			}
		} catch (InvalidInputException exception) {
			// The binary is not an iBoot image, and thus can't be loaded by this loader.
		}
		return result;
	}

	@Override
	protected void load(ByteProvider provider, LoadSpec loadSpec, List<Option> options,
						Program program, TaskMonitor monitor, MessageLog log) {
		FlatProgramAPI flatProgramAPI = new FlatProgramAPI(program, monitor);
		Memory memory = program.getMemory();
		monitor.setMessage("Loading iBoot stage...");
		try {
			Address imageBase = program.getAddressFactory().getDefaultAddressSpace().getAddress(loadSpec.getDesiredImageBase());
			MemoryBlock imageBlock = memory.createInitializedBlock("iBoot", imageBase, provider.length(), (byte) 0, monitor, false);
			imageBlock.setRead(true);
			imageBlock.setExecute(true);
			memory.setBytes(imageBase, provider.readBytes(0, provider.length()));
			flatProgramAPI.addEntryPoint(imageBase);
			flatProgramAPI.disassemble(imageBase);
		} catch (Exception exception) {
			log.appendException(exception);
		}
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
