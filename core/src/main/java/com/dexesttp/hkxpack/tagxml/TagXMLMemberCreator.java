package com.dexesttp.hkxpack.tagxml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dexesttp.hkxpack.data.HKXData;
import com.dexesttp.hkxpack.data.members.HKXArrayMember;
import com.dexesttp.hkxpack.data.members.HKXDirectMember;
import com.dexesttp.hkxpack.data.members.HKXMember;
import com.dexesttp.hkxpack.data.members.HKXPointerMember;
import com.dexesttp.hkxpack.data.members.HKXStringMember;

/**
 * Creates {@link Node} from {@link HKXMember}<br >
 * Please use {@link TagXMLDataCreator} as the handled class.
 */
class TagXMLMemberCreator {
	private Document document;
	private TagXMLDataCreator dataCreator;

	/**
	 * Creates a new {@link TagXMLMemberCreator} from its parent {@link TagXMLDataCreator}.<br >
	 * This shoud only be done by a {@link TagXMLDataCreator}.
	 * @param tagXMLDataCreator
	 */
	TagXMLMemberCreator(TagXMLDataCreator tagXMLDataCreator) {
		this.dataCreator = tagXMLDataCreator;
		this.document = tagXMLDataCreator.document();
	}

	/**
	 * Creates a {@link Node} from a {@link HKXMember}.<br >
	 * @param member
	 * @return
	 */
	Node create(HKXMember member) {
		Element memberNode = document.createElement("hkparam");
		memberNode.setAttribute("name", member.getName());
		switch(member.getType().getFamily()) {
			case DIRECT:
			case COMPLEX:
			case ENUM:
				TagXMLDirectMemberHandler directMemberHandler = new TagXMLDirectMemberHandler();
				String newDirectChild = directMemberHandler.getStringValue((HKXDirectMember<?>) member);
				memberNode.setTextContent(newDirectChild);
				break;
			case STRING:
				String newStringChildContent = ((HKXStringMember) member).get();
				memberNode.setTextContent(newStringChildContent);
				break;
			case POINTER:
				String newPointerChildContent = ((HKXPointerMember) member).get();
				memberNode.setTextContent(newPointerChildContent);
				break;
			case ARRAY:
				memberNode.setAttribute("numelements", "" + ((HKXArrayMember) member).contents().size());
				for(HKXData data : ((HKXArrayMember) member).contents()) {
					Node newChild = dataCreator.create(data);
					memberNode.appendChild(newChild);
				}
				break;
			case OBJECT:
				Node newObjectNode = dataCreator.create(member);
				memberNode.appendChild(newObjectNode);
				break;
			default:
				break;
		}
		return memberNode;
	}
}
