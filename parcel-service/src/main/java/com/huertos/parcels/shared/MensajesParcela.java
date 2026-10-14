package com.huertos.parcels.shared;

public final class MensajesParcela {

    public static final String NOMBRE_AUSENTE =
            "La parcela llegó sin nombre. En esta red cada zona se da de alta con el nombre que los voluntarios usan en campo, por ejemplo «Huerto Roma Norte».";

    public static final String NOMBRE_BLOQUEADO =
            "El nombre de la parcela trae etiquetas, scripts o operadores de consulta. Ese texto no entra al catálogo; escribe solo el nombre de la zona, en lenguaje plano.";

    public static final String NOMBRE_CARACTERES =
            "El nombre de la parcela usa signos que el catálogo no conserva. Admite letras, números y una puntuación simple (punto, coma, apóstrofo, guion o #), como en «Parcela Norte 2».";

    public static final String UBICACION_AUSENTE =
            "La parcela no trae punto GPS. Sin coordenadas no se puede poner en el mapa de la red ni colgarle lecturas de suelo y aire.";

    public static final String LATITUD_AUSENTE =
            "Falta la latitud. La parcela se ubica con latitud y longitud juntas; con un solo componente el punto queda incompleto.";

    public static final String LONGITUD_AUSENTE =
            "Falta la longitud. La parcela se ubica con latitud y longitud juntas; con un solo componente el punto queda incompleto.";

    public static final String LATITUD_NO_FINITA =
            "La latitud no es un número finito. El GPS de la parcela tiene que ser un decimal real, por ejemplo 19.4194.";

    public static final String LONGITUD_NO_FINITA =
            "La longitud no es un número finito. El GPS de la parcela tiene que ser un decimal real, por ejemplo -99.162.";

    public static final String SUELO_AUSENTE =
            "No indicaste el tipo de suelo. Esta red solo abre parcelas arcillosas, limosas o arenosas, porque la textura cambia el riego y la recomendación de cultivo.";

    public static final String SUELO_BLOQUEADO =
            "El tipo de suelo trae etiquetas o operadores de consulta, no una textura. Escribe arcilloso, limoso o arenoso, en texto plano.";

    public static final String RESPONSABLE_AUSENTE =
            "Falta quien responde por la parcela. Cada zona necesita un administrador del huerto, un científico ciudadano o un líder comunitario a cargo.";

    public static final String RESPONSABLE_BLOQUEADO =
            "El nombre del responsable trae etiquetas, scripts o operadores de consulta. Ese texto no se asienta en la ficha; escribe el nombre de la persona en lenguaje plano.";

    public static final String RESPONSABLE_CARACTERES =
            "El responsable trae signos que no corresponden al nombre de una persona de la red. Usa letras, espacios, apóstrofo o guion, como en «Ana López».";

    private MensajesParcela() {
    }

    public static String nombreCorto(int largo) {
        return "El nombre de la parcela quedó en " + largo + " "
                + (largo == 1 ? "carácter visible" : "caracteres visibles")
                + " y el registro pide al menos 3, los necesarios para distinguir la zona de las demás.";
    }

    public static String nombreLargo(int largo) {
        return "El nombre de la parcela trae " + largo
                + " caracteres y la ficha solo conserva 120. Déjalo en el nombre con el que realmente se conoce la zona, no en una descripción larga.";
    }

    public static String responsableCorto(int largo) {
        return "El nombre del responsable quedó en " + largo + " "
                + (largo == 1 ? "carácter visible" : "caracteres visibles")
                + " y la ficha pide al menos 3 para saber quién atiende la parcela.";
    }

    public static String responsableLargo(int largo) {
        return "El nombre del responsable trae " + largo
                + " caracteres y la ficha solo conserva 80. Asienta a la persona a cargo, no un cargo o un párrafo.";
    }

    public static String latitudFuera(double valor) {
        return "La latitud " + Citas.numero(valor)
                + " no sitúa la parcela en un punto geográfico válido. Tiene que quedar entre -90 y 90 grados.";
    }

    public static String longitudFuera(double valor) {
        return "La longitud " + Citas.numero(valor)
                + " no sitúa la parcela en un punto geográfico válido. Tiene que quedar entre -180 y 180 grados.";
    }

    public static String sueloDesconocido(String citado) {
        return "El suelo " + citado
                + " no lo trabaja esta red. Solo se dan de alta parcelas arcillosas, limosas o arenosas; de esa textura dependen el riego y la recomendación de cultivo.";
    }
}
