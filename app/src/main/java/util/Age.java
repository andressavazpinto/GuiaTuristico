package util;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.*;

public class Age {
//Calcula a Idade baseado em String. Exemplo: calculaIdade("20/08/1977","dd/MM/yyyy");
    public int calculaIdade(String dataNasc, String pattern){

        String array[];
        array = dataNasc.split("/");
        String aux = array[0]+"-"+array[1]+"-"+array[2];

        DateFormat sdf = new SimpleDateFormat(pattern);
        Date dataNascInput = null;

        try {
            dataNascInput= sdf.parse(aux);
        } catch (Exception e) {}

        Calendar dateOfBirth = new GregorianCalendar();
        dateOfBirth.setTime(dataNascInput);

// Cria um objeto calendar com a data atual
        Calendar today = Calendar.getInstance();
// Obtém a idade baseado no ano
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        dateOfBirth.add(Calendar.YEAR, age);

        if (today.before(dateOfBirth)) {
            age--;
        }

        return age;
    }

    public boolean validateDate(String date) {
        boolean aux;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false);
        try {
            df.parse(date);
            aux = true;
        } catch (ParseException ex) {
            aux = false;
        }
        return aux;
    }
}