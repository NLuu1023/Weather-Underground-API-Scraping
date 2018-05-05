public class Weather {
    private String time;
    private String weather;
    private String temperature;
    private String humidity;
    private String wind;
    private String dewPointF;
    private String dewPointC;
    private String feelLikeTemp;
    private String precip;

    public Weather(){}

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public void setDewPointF(String dewPointF) {
        this.dewPointF = dewPointF;
    }

    public void setFeelLikeTemp(String feelLikeTemp) {
        this.feelLikeTemp = feelLikeTemp;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setDewPointC(String dewPointC) {
        this.dewPointC = dewPointC;
    }

    public void setPrecip(String precip) {
        this.precip = precip;
    }

    public String getTime() {
        return time;
    }

    public String getWeather() {
        return weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWind() {
        return wind;
    }

    public String getDewPointF() {
        return dewPointF;
    }

    public String getDewPointC() {
        return dewPointC;
    }

    public String getFeelLikeTemp() {
        return feelLikeTemp;
    }

    public String getPrecip() {
        return precip;
    }
}
