package model;

import java.io.Serializable;

public class HistoryTransaction implements Serializable { // allow transfer via RMI

	private static final long serialVersionUID = 1L;
	
	private String from;
	private String to;
	private Double amount;
	private String note;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    HistoryTransaction that = (HistoryTransaction) o;
	    return from.equals(that.from)
	            && to.equals(that.to)
	            && amount.equals(that.amount)
	            && note.equals(that.note);
	}

	@Override
	public int hashCode() {
	    return java.util.Objects.hash(from, to, amount, note);
	}

}
