package com.softcod.testedadoscorona.processadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.softcod.testedadoscorona.utils.RetrieveHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Processos {
    Context ctxt;
    String[] saida =new String[14];
    RetrieveHttp http;
    JSONObject json = null;

    int mortes;  int casos;
    double latitude = 0.0, longitude = 0.0;
    int dia; int mes;  int ano;


    Integer[][] dados = new Integer[200][2];

    public Processos(Context c){
        super();
        this.ctxt =c;

    }


    public void registrarConsulta(String latitude, String longitude, int dia, int mes, int ano){
        RetrieveHttp http = new RetrieveHttp();
        JSONObject json = null;
        try {
            json = http.execute(
                    "http://softcod.com.br/api/teste/modificar.php?",
                    "POST", "do=finalizarPesquisa"
                            +"&data=" + getDia() +"/" + getMes()+"/" + getAno()
                            +"&casos=" + getCasos()
                            +"&mortes=" + getMortes()
                            +"&long=" + getLongitude()
                            +"&lat=" +getLatitude()
            ).get();

            Log.d("JSON____", String.valueOf(json));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Log.d("JSON____", String.valueOf(json));
        }
    }

    public void carregarBanco() {
        RetrieveHttp http = new RetrieveHttp();
        JSONObject json = null;
        try {
            json = http.execute(
                    "http://softcod.com.br/api/teste/modificar.php/?",
                    "POST", "do=criar"
            ).get();

            Log.d("JSON____", String.valueOf(json));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Log.d("JSON____", String.valueOf(json));
        }
    }

    public String calculeMediaMovel(int dia, int mes, int ano){

        int i = 0;
        int r1 = 0, r2 = 0;
        int diasSemana = 14;

        do{
            saida[i] = "\nEm: " + dia+"/"+mes+"/"+ano +
                    " \nfoi contabilizados \n"+
                    dados[i][0]
                    +"\n casos da covide\n---------------\n"
                    +"MORTOS\n" + dados[i][1] ;
            i++;

            if(dia>1) {dia -= 1;}
            else{mes -=1;dia = quantosDiasMes(mes, ano);}
        }while (i<14);

        for (;diasSemana>=8; diasSemana--){r2 += dados[diasSemana][0];
            r1 += dados[diasSemana][1]; }

        int semOco1 = r1/7;
        int semMor1 = r2/7;

        int m1 = 0, m2 = 0;

        for (; diasSemana>=0; diasSemana--){ m1 += dados[diasSemana][0];
            m2 += dados[diasSemana][1]; }

        int semOco2 = m1/7;
        int semMor2 = m2/7;

        int flutuacao =(semOco2-semOco1);
        int obitos =(semMor1-semMor2);

        return "Durante a 1ª semana:\n"+
                saida[0] +saida[1] +saida[2] +saida[3] +
                saida[4] + saida[5] +saida[6] +
                "\n\n\nMédia variavel da primeira semana:\n\n"+
                +semOco1+
                "\n---------\nDurante a 2ª semana:\n"+
                saida[7] +saida[8] +saida[9] +saida[10] +
                saida[11] + saida[12] +saida[13] +
                "\n\n\nMédia variavel da segunda semana:\n\n"+
                semOco2+
                "\n\n\n--------\n\nNotamos uma flutuação de casos confirmados:\n"
                +flutuacao+
                "\n\n\n--------\n\nNotamos uma flutuação de obitos:\n"
                +obitos;
    }

    public String dadosMesPassado(){

        int maxCasos=0, maxMortes = 0;
        Integer[] casos = new Integer[30];
        Integer[] mortes = new Integer[30];
        int n=0;
        do{
            casos[n] = dados[n][0];
            mortes[n] = dados[n][1];
            n++;
        }while (n<30);

        for(int i = 1; i <casos.length; i++){
            if(casos[i]>maxCasos){
                maxCasos = casos[i];
            }
        }

        for(int i = 1; i <mortes.length; i++){
            if(mortes[i]>maxMortes){
                maxMortes = mortes[i];
            }
        }
        setMortes(maxMortes);
        setCasos(maxCasos);
        return "Casos: " + maxCasos + ", \nmortes: " + maxMortes;

    }

    public int quantosDiasMes(int mes, int ano){
        //levando que em conta que se trata de uma pesquisa
        //sobre a corona, foi feito uma formula simles do
        //ano bissexto

        Integer[] meses = new Integer[12];
        meses[0]=31; meses[1]=ano%400==0?29:28;meses[2]=31;
        meses[3]=30; meses[4]=31;meses[5]=30;
        meses[6]=31; meses[7]=31; meses[8]=30;
        meses[9]=31; meses[10]=30;meses[11]=31;
        Log.d("\n\n\nMES",""+ mes);
        //subitrair para controlar por ser de 0 a 11
        return meses[mes-1];
    }


    @SuppressLint("RestrictedApi")
    public void getDadoArray(int dia, int mes, int ano){
        String zm="", zd = "";
        int i = 0;

        //CONTOLAR A QUANTIDADE (6 MESES 186 COM MARGEM)
        do{
            //CONTROLAR O ANO

            do {
                //CONTOLAR O MES
                if(mes<10){ zm="0"; }else{zm="";}
                do {
                    if(dia<10){ zd="0"; }else {zd ="";}
                    http = new RetrieveHttp();

                    //a pessoa seleciona, envia para a api, e vou colocar outro borão
                    //para ela poder excluir

                    try {
                        json = http.execute(
                                "http://softcod.com.br/api/teste/filtrar.php/?",
                                "POST",
                                "tabela=dados_gerais" +
                                        "&coluna=data" +
                                        "&pesquisa=" + ano + "-" + zm + mes + "-" + zd + dia
                        ).get();

                        dados[i][0] = json.getInt("q");
                        dados[i][1] = json.getInt("m");
                        Log.d("Quantidade", "\n\n\n\nQuantidade de casos: " +json.getInt("q"));

                    } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                    i++;
                    dia--;
                } while (dia > 1);
                dia = quantosDiasMes(mes, ano);
                mes--;

            } while (mes >= 1 && mes <= quantosDiasMes(mes, ano) );
            mes = 12;
            ano--;
        }while (i >=  186);

    }


    /*private boolean getDadoArray(int dia, int mes, int ano){
        int c = 0;
        String zm="", zd = "";

        //int d =dia;
        int a = ano;
        int m;
*/
      /*  int countDay = 0;

        for(int i=0;i<6;i++){
            if(mes<1){mes =12;}
            countDay +=quantosDiasMes(ano, mes--);
        }
*/
   /*     if(mes-6<=0){ m = (mes-6)+12; a--;}else {m = mes-6;}
        Log.d("Resulado \n\n\n", ""+m);

        //CONTROLAR A QUANTIDADE (6 MESES 186 COM MARGEM)
        do{

            //CONTROLAR O ANO

            do {

                //CONTOLAR O MES
                zm = m<10?"0":"";

                //do {
                for(int x =dia; x <= quantosDiasMes(a, mes); x++){

                    zd = dia<10? "0":"";
                    http = new RetrieveHttp();

                    try {
                        json = http.execute(
                                "http://softcod.com.br/api/teste/filtrar.php/?",
                                "POST",
                                "tabela=dados_gerais" +
                                        "&coluna=data" +
                                        "&pesquisa=" + a + "-" + zm + m + "-" + zd + x
                        ).get();

                        dados[c][0] = Math.max(json.getInt("q"), 0);
                        dados[c][1] = Math.max(json.getInt("m"), 0);

                        Log.d("Quantidade", "\n\n\n\nQuantidade de casos: " +json.getInt("q"));

                    } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                    c += 1;


                    // countDay--;
                } //while (d >= quantosDiasMes(m, a));
                dia = quantosDiasMes(a, mes);
                m += 1;


            } while ((m>=1 && m <=12));
            a++;
            m = 1;

            Log.d("\n\n\n\nContagem ANO", "VALOR: "+m +" outro " + mes );
        }while (m != mes || a != ano);

        return true;
    }*/


    public int getMortes() {
        return mortes;
    }

    public void setMortes(int mortes) {
        this.mortes = mortes;
    }

    public int getCasos() {
        return casos;
    }

    public void setCasos(int casos) {
        this.casos = casos;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }


}
