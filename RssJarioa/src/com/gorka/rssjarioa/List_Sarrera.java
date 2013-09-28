package com.gorka.rssjarioa;


public class List_Sarrera {
	private String egune;
	private String tituloa; 
	private String lekua;
    private String ordue;
    private String link;
    private int id;
    private int idImagen;

    public List_Sarrera( String tituloa,String egune, String ordue, String lekua,int id) {
	    this.egune = egune;
	    this.tituloa = tituloa; 
	    this.lekua = lekua;
        this.ordue = ordue;
        this.id = id;
	}

    public List_Sarrera(String tituloa, String link, int idImagen) {
        this.tituloa = tituloa;
        this.link = link;
        this.idImagen = idImagen;
    }
	
	public String get_tituloa() { 
	    return tituloa; 
	}
	
	public String get_lekua() { 
	    return lekua; 
	}

    public String get_ordue(){
        return ordue;
    }
	
	public String get_egune() {
	    return egune;
	}

    public String get_link(){
        return link;
    }

    public int get_id(){
        return id;
    }

    public int get_idImagen() {
        return idImagen;
    }
}
