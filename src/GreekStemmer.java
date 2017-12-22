
import java.util.Locale;



public class GreekStemmer {

    /**
     * Buffer for the terms while stemming them.
     */
    private StringBuffer sb = new StringBuffer();
    /**
     * Indicates if a term is handled as a noun.
     */
    private boolean uppercase = false;
    /**
     * Amount of characters that are removed with <tt>substitute()</tt> while stemming.
     */
    private int substCount = 0;

    public GreekStemmer() {
    }

    /**
     * Stemms the given term to an unique <tt>discriminator</tt>.
     *
     * @param term  The term that should be stemmed.
     * @return      Discriminator for <tt>term</tt>
     */

    public String stem(String term) {

        // Check if the term is stemmable
        //if (!isStemmable(term)){
        //    return term;
        //}

        //Check if term is numeric
      //DIKH MOY ALLAGH GIATI XTYPAGE TO INDEXfILES
      //  if (term.matches("^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?$"))
      //      return "";
    	
        // Remove first level suffixes only if the term is 4 letters or more
        if (term.length() >= 4) {

            // Remove the 3 letter suffixes
            if (term.endsWith("ΟΥΣ")||
                term.endsWith("ΕΙΣ")||
                term.endsWith("ΕΩΝ")||
                term.endsWith("ΟΥΝ")){
                term = term.substring(0,term.length() - 3);}

            // Remove the 2 letter suffixes
            else if (term.endsWith("ΟΣ")||
                     term.endsWith("ΗΣ")||
                     term.endsWith("ΕΣ")||
                     term.endsWith("ΩΝ")||
                     term.endsWith("ΟΥ")||
                     term.endsWith("ΟΙ")||
                     term.endsWith("ΑΣ")||
                     term.endsWith("ΩΣ")||
                     term.endsWith("ΑΙ")||
                     term.endsWith("ΥΣ")||
                     term.endsWith("ΟΝ")||
                     term.endsWith("ΑΝ")||
                     term.endsWith("ΕΙ")){

                     term = term.substring(0,term.length() - 2);}

            // Remove the 1 letter suffixes
            else if (term.endsWith("Α")||
                     term.endsWith("Η")||
                     term.endsWith("Ο")||
                     term.endsWith("Ε")||
                     term.endsWith("Ω")||
                     term.endsWith("Υ")||
                     term.endsWith("Ι")){

                     term = term.substring(0,term.length() - 1);}
        }
        return term;
	}
}