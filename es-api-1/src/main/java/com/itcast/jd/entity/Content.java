package com.itcast.jd.entity;

import java.io.Serializable;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Content implements Serializable{

	private String img ;
	private String title;
	private String price;
}
