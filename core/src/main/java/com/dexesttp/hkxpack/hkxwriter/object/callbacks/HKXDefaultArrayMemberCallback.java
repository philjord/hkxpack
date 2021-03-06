package com.dexesttp.hkxpack.hkxwriter.object.callbacks;

import java.io.IOException;
import java.util.List;

import com.dexesttp.hkxpack.data.HKXData;
import com.dexesttp.hkxpack.data.members.HKXArrayMember;
import com.dexesttp.hkxpack.data.members.HKXMember;
import com.dexesttp.hkxpack.hkx.HKXUtils;
import com.dexesttp.hkxpack.hkx.types.MemberSizeResolver;
import com.dexesttp.hkxpack.hkxwriter.object.HKXMemberHandler;
import com.dexesttp.hkxpack.hkxwriter.object.HKXMemberHandlerFactory;

public class HKXDefaultArrayMemberCallback implements HKXArrayMemberCallback {
	private final HKXArrayMember arrMember;
	private final HKXMemberHandlerFactory memberHandlerFactory;

	public HKXDefaultArrayMemberCallback(HKXArrayMember arrMember, HKXMemberHandlerFactory memberHandlerFactory) {
		this.arrMember = arrMember;
		this.memberHandlerFactory = memberHandlerFactory;
	}

	@Override
	public long process(List<HKXMemberCallback> memberCallbacks, long position) throws IOException {
		long newPos = position;
		long memberSize = MemberSizeResolver.getSize(arrMember.getSubType());
		for(HKXData data : arrMember.contents()) {
			if(data instanceof HKXMember) {
				HKXMember internalMember = (HKXMember) data;
				HKXMemberHandler memberHandler = memberHandlerFactory.create(internalMember.getType(), 0);
				memberCallbacks.add(memberHandler.write(internalMember, newPos));
				newPos += memberSize;
			}
		}
		return HKXUtils.snapLine(newPos) - position;
	}

}
