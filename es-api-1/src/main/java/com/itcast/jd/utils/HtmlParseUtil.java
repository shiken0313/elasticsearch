package com.itcast.jd.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.itcast.jd.entity.Content;


@Component
public class HtmlParseUtil {



	public static List<Content> parseJD(String KeyWords) throws IOException {
		String url = "https://search.jd.com/Search?keyword=" + KeyWords+"&enc=utf-8";
		Document document = Jsoup.parse(new URL(url), 30000);
		Element element = document.getElementById("J_goodsList");
		Elements elements = element.getElementsByTag("li");
		List<Content> list = new ArrayList<>();
		for (Element el : elements) {
			String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
			String price = el.getElementsByClass("p-price").eq(0).text();
			String title = el.getElementsByClass("p-name").eq(0).text();
			Content content = new Content(img,title,price);
			list.add(content);
		}
		return list;
	}
}
