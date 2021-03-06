package com.dexesttp.hkxpack.hkxwriter.object.callbacks;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.dexesttp.hkxpack.data.HKXData;
import com.dexesttp.hkxpack.data.members.HKXArrayMember;
import com.dexesttp.hkxpack.data.members.HKXMember;
import com.dexesttp.hkxpack.data.members.HKXStringMember;
import com.dexesttp.hkxpack.hkx.HKXUtils;
import com.dexesttp.hkxpack.hkx.data.DataInternal;
import com.dexesttp.hkxpack.hkx.types.MemberSizeResolver;

public class HKXStringArrayMemberCallback implements HKXArrayMemberCallback {
	private final List<DataInternal> data1;
	private final HKXArrayMember arrMember;
	private RandomAccessFile outFile;

	public HKXStringArrayMemberCallback(List<DataInternal> data1, HKXArrayMember arrMember, RandomAccessFile outFile) {
		this.data1 = data1;
		this.arrMember = arrMember;
		this.outFile = outFile;
	}

	@Override
	public long process(List<HKXMemberCallback> memberCallbacks, long position) throws IOException {
		long newPos = position;
		long memberSize = MemberSizeResolver.getSize(arrMember.getSubType());
		List<HKXMemberCallback> internalCallbacks = new ArrayList<>();
		for(HKXData data : arrMember.contents()) {
			if(data instanceof HKXMember) {
				HKXMember internalMember = (HKXMember) data;
				internalCallbacks.add(stringHandler((HKXStringMember) internalMember, newPos));
				newPos += memberSize;
			}
		}
		internalCallbacks.add((callbacks, newPosition) -> {return HKXUtils.snapLine(newPosition) - newPosition;});
		memberCallbacks.addAll(0, internalCallbacks);
		return newPos - position;
	}
	
	public HKXMemberCallback stringHandler(HKXStringMember internalMember, long pos) {
		final DataInternal stringData = new DataInternal();
		stringData.from = pos;
		return (callbacks, position) -> { 
			stringData.to = position;
			data1.add(stringData);
			outFile.seek(position);
			outFile.writeBytes(internalMember.get());
			outFile.writeByte(0x00);
			long outSize = internalMember.get().length() + 1;
			return outSize + ((position + outSize) % 2);
		};
	}
}
