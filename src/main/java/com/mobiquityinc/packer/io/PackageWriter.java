package com.mobiquityinc.packer.io;

import com.mobiquityinc.packer.domain.Package;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.function.Function;

/**
 * Writes built package to the given output stream using the provided {@link #formatter}.
 */
public class PackageWriter implements AutoCloseable {

    private PrintWriter writer;
    private Function<Package, String> formatter;

    public PackageWriter(OutputStream out, Function<Package, String> formatter) {
        this.writer = new PrintWriter(out);
    }

    public PackageWriter(OutputStream out) {
        this(out, PackageWriter::defaultFormatter);
    }

    /**
     * Writes pack to the underlying output.
     *
     * @param pack pack to write
     */
    public void write(Package pack) {
        writer.println(defaultFormatter(pack));
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }

    private static String defaultFormatter(Package pack) {
        return pack == null || pack.getThings().size() == 0 ? "-" :
            pack.getThings().stream().map(t -> String.valueOf(t.getIndex())).reduce((p, c) -> p + "," + c).orElse("");
    }
}
