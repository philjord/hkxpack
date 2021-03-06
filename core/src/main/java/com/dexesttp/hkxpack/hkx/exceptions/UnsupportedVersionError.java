package com.dexesttp.hkxpack.hkx.exceptions;

/**
 * Thrown if a file with a non-supported version was attempted to be read.
 */
public class UnsupportedVersionError extends Exception {
	private static final long serialVersionUID = -1869309792519998570L;

	public UnsupportedVersionError(String versionName) {
		super("File version not supported : " + versionName);
	}
}
