package com.dexesttp.hkxpack.tagwriter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dexesttp.hkxpack.data.HKXData;
import com.dexesttp.hkxpack.data.members.HKXArrayMember;
import com.dexesttp.hkxpack.data.members.HKXDirectMember;
import com.dexesttp.hkxpack.data.members.HKXPointerMember;
import com.dexesttp.hkxpack.data.members.HKXStringMember;

/**
 * Handles the conversion between a {@link HKXArrayMember} and its contents as an {@link Element}.
 */
class TagXMLArrayMemberHandler {
	private final TagXMLDataCreator dataCreator;
	private static final int MAX_LENGTH_PER_LINE = 64;

	TagXMLArrayMemberHandler(TagXMLDataCreator dataCreator) {
		this.dataCreator = dataCreator;
	}
	
	/**
	 * Fills the given {@link Element} with the {@link HKXArrayMember}'s contents.
	 * @param memberNode the {@link Element} to fill.
	 * @param member the {@link HKXArrayMember} to get the data from.
	 */
	void fillArray(Element memberNode, HKXArrayMember member) {
		memberNode.setAttribute("numelements", "" + ((HKXArrayMember) member).contents().size());
		switch(member.getSubType().getFamily()) {
			case DIRECT:
			case COMPLEX:
			case ENUM:
				handleEnumOutType(memberNode, member);
				break;
			case STRING:
				handleStringOutType(memberNode, member);
				break;
			case POINTER:
				handlePtrOutType(memberNode, member);
				break;
			default:
				handleNodeOutType(memberNode, member);
				break;
		}
	}

	private void handleStringOutType(Element memberNode, HKXArrayMember member) {
		for(HKXData data : member.contents()) {
			HKXStringMember subMember = (HKXStringMember) data;
			String subMemberString = subMember.get();
			Element stringNode = dataCreator.document().createElement("hkcstring");
			stringNode.setTextContent(subMemberString);
			memberNode.appendChild(stringNode);
		}
	}

	private void handleNodeOutType(Element memberNode, HKXArrayMember member) {
		for(HKXData data : member.contents()) {
			Node newChild = dataCreator.create(data);
			memberNode.appendChild(newChild);
		}
	}

	private void handleEnumOutType(Element memberNode, HKXArrayMember member) {
		TagXMLDirectMemberHandler directMemberHandler = new TagXMLDirectMemberHandler();
		String accu = "";
		String contents = "";
		for(HKXData data : member.contents()) {
			HKXDirectMember<?> subMember = (HKXDirectMember<?>) data;
			String subMemberString = directMemberHandler.getStringValue(subMember);
			if((contents + subMemberString).length() > MAX_LENGTH_PER_LINE ) {
				if(contents.isEmpty())
					accu += "\n";
				else
					accu += "\n" + contents.substring(0, contents.length() - 1);
				contents = "";
			}
			contents += subMemberString + " ";
		}
		if(!contents.isEmpty()) {
			if(!accu.isEmpty())
				accu += "\n";
			accu += contents.substring(0, contents.length() - 1);
		}
		memberNode.setTextContent(accu);
	}


	private void handlePtrOutType(Element memberNode, HKXArrayMember member) {
		String accu = "\n";
		for(HKXData data : member.contents()) {
			HKXPointerMember subMember = (HKXPointerMember) data;
			String subMemberString = subMember.get();
			accu += subMemberString + "\n";
		}
		memberNode.setTextContent(accu);
	}
}
