package pizzaaxx.bteconosur.country;

public class CountryManager {

    private Country ARGENTINA = new Country("argentina");
    private Country BOLIVIA = new Country("bolivia");
    private Country CHILE = new Country("chile");
    private Country PARAGUAY = new Country("paraguay");
    private Country PERU = new Country("peru");
    private Country URUGUAY = new Country("uruguay");
    private Country GLOBAL = new Country("global");

    // TODO IMPLEMENT THIS

    public Country get(String country) {
        switch (country.toLowerCase()) {
            case "ar":
            case "argentina":
                return ARGENTINA;
            case "bo":
            case "bolivia":
                return BOLIVIA;
            case "cl":
            case "chile":
                return CHILE;
            case "py":
            case "paraguay":
                return PARAGUAY;
            case "pe":
            case "peru":
                return PERU;
            case "uy":
            case "uruguay":
                return URUGUAY;
            case "gl":
            case "global":
                return GLOBAL;
            default:
                return null;
        }
    }

}
