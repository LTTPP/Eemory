package com.prairie.eevernote.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;

public class ConfigContentProposalProvider extends SimpleContentProposalProvider {

	private String byOperator;

	public ConfigContentProposalProvider(String[] proposals) {
		super(proposals);
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		if (!StringUtils.isEmpty(byOperator)) {
			if (contents.contains(byOperator)) {
				contents = contents.substring(contents.lastIndexOf(byOperator) + 1).trim();
			}
		}
		return super.getProposals(contents, position);
	}

	public String getByOperator() {
		return byOperator;
	}

	public void setByOperator(String byOperator) {
		this.byOperator = byOperator;
	}

}
