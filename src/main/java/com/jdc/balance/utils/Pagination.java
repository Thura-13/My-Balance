package com.jdc.balance.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public class Pagination {
	
	private int current;
	private int total;
	private String url;
	private boolean first;
	private boolean last;
	private List<Integer> pages;
	private Map<String, String> params;
	private List<Integer> sizes;
	
	public static  Builder builder(String url) {
		return new Builder(url);
	}
	
	public static class Builder{
		
		private int current;
		private int total;
		private String url;
		private boolean first;
		private boolean last;
		private Map<String, String> params;
		private List<Integer> sizes;

		public Builder(String url) {
			this.url = url;
		}
		
		public <T> Builder pages(Page<T> data) {
			this.current = data.getNumber();
			this.total = data.getTotalPages();
			this.first = data.isFirst();
			this.last = data.isLast();
			return this;
		}
		
		public Builder sizes(List<Integer> sizes) {
			this.sizes=sizes;
			return this;
		}
		
		public Builder params(Map<String, String> params) {
			this.params = params;
			return this;
		}
	
		public  Pagination build() {
			return new Pagination(current, total, url, first, last,params,sizes);
		}
		
		
	}
	private Pagination(int current, int total, String url, boolean first,
			boolean last,	Map<String, String> params, List<Integer> sizes
) {
		super();
		this.current = current;
		this.total = total;
		this.url = url;
		this.first = first;
		this.last = last;
		this.params = params;
		this.sizes = sizes;
		
		if(params == null) {
			params = new HashMap<String, String>();
		}
		
		pages = new ArrayList<Integer>();
		pages.add(current);
		
		while(pages.size() < 3 && pages.get(0) > 0) {
			pages.add(0, pages.get(0)-1);
		}
		
		while(pages.size() < 5 && pages.get(pages.size() -1) < total -1 ) {
			pages.add(pages.get(pages.size()-1)+1);
		}
		
		while(pages.size() < 5 && pages.get(0) > 0) {
			pages.add(0, pages.get(0)-1);
		}
	}
	public int getCurrent() {
		return current;
	}
	public int getTotal() {
		return total;
	}
	public String getUrl() {
		return url;
	}
	public boolean isFirst() {
		return first;
	}
	public boolean isLast() {
		return last;
	}
	public List<Integer> getPages() {
		return pages;
	}
	
	public List<Integer> getSizes() {
		return sizes;
	}
	
	public String getParams() {
		return params.entrySet().stream()
				.map(e->"%s=%s".formatted(e.getKey(),e.getValue()))
				.reduce("",(a,b)-> "%s&%s".formatted(a,b) );
	}
	
	
}
