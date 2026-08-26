// Dump references, disassembly, and decompiler output for selected addresses.
// @category JieLi

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import ghidra.app.decompiler.DecompInterface;
import ghidra.app.decompiler.DecompileResults;
import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.FunctionManager;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.InstructionIterator;
import ghidra.program.model.symbol.Reference;
import ghidra.program.model.symbol.ReferenceIterator;

public class DumpTargetRefs extends GhidraScript {
    @Override
    protected void run() throws Exception {
        String[] args = getScriptArgs();
        if (args.length < 2) {
            throw new IllegalArgumentException(
                "usage: DumpTargetRefs.java OUTPUT ADDRESS [ADDRESS ...]"
            );
        }

        File output = new File(args[0]);
        FunctionManager functions = currentProgram.getFunctionManager();
        Set<Function> targets = new LinkedHashSet<>();

        try (PrintWriter writer = new PrintWriter(output, "UTF-8")) {
            writer.printf("program: %s%n", currentProgram.getName());
            writer.printf("language: %s%n%n", currentProgram.getLanguageID());

            for (int i = 1; i < args.length; i++) {
                Address address = toAddr(args[i]);
                writer.printf("target %s%n", address);
                ReferenceIterator refs = currentProgram.getReferenceManager()
                    .getReferencesTo(address);
                int count = 0;
                while (refs.hasNext()) {
                    Reference ref = refs.next();
                    Function function = functions.getFunctionContaining(ref.getFromAddress());
                    writer.printf(
                        "  ref %-12s type=%-16s function=%s%n",
                        ref.getFromAddress(), ref.getReferenceType(),
                        function == null ? "<none>" : function.getName()
                    );
                    if (function != null) {
                        targets.add(function);
                    }
                    count++;
                }
                writer.printf("  total references: %d%n%n", count);
            }

            DecompInterface decompiler = new DecompInterface();
            decompiler.openProgram(currentProgram);

            for (Function function : targets) {
                writer.printf(
                    "================================================================%n" +
                    "function %s @ %s, body %s%n%n",
                    function.getName(), function.getEntryPoint(), function.getBody()
                );

                InstructionIterator instructions = currentProgram.getListing()
                    .getInstructions(function.getBody(), true);
                while (instructions.hasNext()) {
                    Instruction insn = instructions.next();
                    writer.printf("%s  %-20s %s%n", insn.getAddress(),
                        bytes(insn.getBytes()), insn);
                }

                writer.println();
                DecompileResults result = decompiler.decompileFunction(function, 60, monitor);
                if (result.decompileCompleted()) {
                    writer.println(result.getDecompiledFunction().getC());
                }
                else {
                    writer.printf("decompiler error: %s%n", result.getErrorMessage());
                }
                writer.println();
            }
            decompiler.dispose();
        }
    }

    private static String bytes(byte[] values) {
        StringBuilder text = new StringBuilder();
        for (byte value : values) {
            text.append(String.format("%02x ", value & 0xff));
        }
        return text.toString();
    }
}
