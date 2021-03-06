package com.dexesttp.hkxpack.tagreader.members;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dexesttp.hkxpack.data.members.HKXMember;
import com.dexesttp.hkxpack.descriptor.exceptions.ClassFileReadError;
import com.dexesttp.hkxpack.descriptor.members.HKXMemberTemplate;
import com.dexesttp.hkxpack.l10n.SBundle;
import com.dexesttp.hkxpack.tagreader.TagXMLNodeHandler;
import com.dexesttp.hkxpack.tagreader.exceptions.InvalidTagXMLException;

public class TagXMLEmbeddedObjectHandler implements TagXMLContentsHandler {
	private final TagXMLNodeHandler nodeHandler;

	public TagXMLEmbeddedObjectHandler(TagXMLNodeHandler nodeHandler) {
		this.nodeHandler = nodeHandler;
	}

	@Override
	public HKXMember handleNode(Node member, HKXMemberTemplate memberTemplate) throws ClassFileReadError, InvalidTagXMLException {
		String target = memberTemplate.target;
		NodeList children = member.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node objectNode = children.item(i);
			if(objectNode.getNodeName().equals("hkobject")) {
				return handleNode(objectNode, target);
			}
		}
		throw new InvalidTagXMLException(SBundle.getString("error.tag.read.member") + memberTemplate.name);
	}

	HKXMember handleNode(Node member, String target) throws ClassFileReadError, InvalidTagXMLException {
		return nodeHandler.handleSubObject(member, target);
	}
}
