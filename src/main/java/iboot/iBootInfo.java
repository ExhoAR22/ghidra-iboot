package iboot;

import ghidra.app.util.bin.ByteProvider;
import ghidra.util.exception.InvalidInputException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class iBootInfo {
    private static final long DESCRIPTION_OFFSET = 0x200;
    private static final int DESCRIPTION_SIZE = 0x40;
    private static final long EDITION_OFFSET = 0x240;
    private static final int EDITION_LENGTH = 0x40;
    private static final long VERSION_OFFSET = 0x280;
    private static final long VERSION_SIZE = 0x40;
    private static final long BASE_ADDRESS_OFFSET_OLD = 0x318;
    private static final long BASE_ADDRESS_OFFSET_NEW = 0x300;
    private static final int BASE_ADDRESS_SIZE = 8;
    private static final int NEW_VERSION = 6603;

    private final String description;
    private final String edition;
    private final String version;
    private final byte[] baseAddressArea;

    public iBootInfo(ByteProvider provider) throws IOException {
        this.description = new String(provider.readBytes(DESCRIPTION_OFFSET, DESCRIPTION_SIZE),
                StandardCharsets.US_ASCII);
        this.edition = new String(provider.readBytes(EDITION_OFFSET, EDITION_LENGTH), StandardCharsets.US_ASCII);
        this.version = new String(provider.readBytes(VERSION_OFFSET, VERSION_SIZE), StandardCharsets.US_ASCII);

        long minimalBaseAddressOffset = Math.min(BASE_ADDRESS_OFFSET_NEW, BASE_ADDRESS_OFFSET_OLD);
        long maximalBaseAddressOffset = Math.max(BASE_ADDRESS_OFFSET_NEW, BASE_ADDRESS_OFFSET_OLD);
        this.baseAddressArea = provider.readBytes(minimalBaseAddressOffset,
                maximalBaseAddressOffset - minimalBaseAddressOffset + BASE_ADDRESS_SIZE);
    }

    /**
     * @return the binary's iBoot stage (SecureROM, LLB, iBoot, ...)
     * @throws InvalidInputException in case the binary is not a valid iBoot stage
     */
    public String getStage() throws InvalidInputException {
        for (String stage : Consts.STAGES) {
            if (description.startsWith(stage + " for ")) {
                return stage;
            }
        }
        throw new InvalidInputException();
    }

    /**
     * @return the name of the device the binary was built for. In SecureROM images this is the name of SoC, otherwise
     *         the name of the board
     * @throws InvalidInputException in case the binary is not a valid iBoot stage
     */
    public String getDevice() throws InvalidInputException {
        String descriptionWithoutType = this.description.substring((this.getStage() + " for ").length());
        return descriptionWithoutType.substring(0, descriptionWithoutType.indexOf(',')).toLowerCase();
    }

    /**
     * @return whether or not the binary is supported by this extension
     * @throws InvalidInputException in case the binary is not a valid iBoot stage
     */
    public boolean is64Bit() throws InvalidInputException {
        String device = this.getDevice();
        for (String unsupportedDevice : Consts.DEVICES_32BIT) {
            if (device.equals(unsupportedDevice)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the binary's iBoot version
     * @throws InvalidInputException in case the binary is not a valid iBoot stage
     */
    public String getVersion() throws InvalidInputException {
        if (!version.startsWith(Consts.VERSION_PREFIX)) {
            throw new InvalidInputException();
        }

        return this.version.substring(Consts.VERSION_PREFIX.length());
    }

    /**
     * @return the binary's "edition", this is normally "RELEASE"
     */
    public String getEdition() {
        return this.edition;
    }

    /**
     * @return the binary's base address as deduced by it's version
     * @throws InvalidInputException in case the binary is not a valid iBoot stage
     */
    public long getBaseAddress() throws InvalidInputException {
        String versionString = this.getVersion();
        if (versionString.contains(".")) {
            versionString = versionString.split("\\.")[0];
        }

        if (Integer.parseInt(versionString) < NEW_VERSION) {
            return Utils.toLittleEndianLong(this.baseAddressArea,
                    (int)(BASE_ADDRESS_OFFSET_OLD - BASE_ADDRESS_OFFSET_NEW), BASE_ADDRESS_SIZE);
        } else {
            return Utils.toLittleEndianLong(this.baseAddressArea, 0, BASE_ADDRESS_SIZE);
        }
    }
}
