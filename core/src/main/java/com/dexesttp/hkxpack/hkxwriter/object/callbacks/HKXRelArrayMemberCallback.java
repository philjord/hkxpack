package com.dexesttp.hkxpack.hkxwriter.object.callbacks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.dexesttp.hkxpack.resources.ByteUtils;

public class HKXRelArrayMemberCallback implements HKXMemberCallback {
	private final HKXArrayMemberCallback callbackProcessor;
	private final ByteBuffer outFile;
	private final long classPos;
	private final long argPos;

	public HKXRelArrayMemberCallback(HKXArrayMemberCallback callbackProcessor, ByteBuffer outFile, long classPos, long argPos) {
		this.callbackProcessor = callbackProcessor;
		this.outFile = outFile;
		this.classPos = classPos;
		this.argPos = argPos;
	}

	@Override
	public long process(List<HKXMemberCallback> memberCallbacks, long position) throws IOException {
		byte[] offset = ByteUtils.fromLong(position - classPos, 2);
		outFile.position((int) (classPos + argPos + 2));
		outFile.put(offset);
		return callbackProcessor.process(memberCallbacks, position);
	}

}
