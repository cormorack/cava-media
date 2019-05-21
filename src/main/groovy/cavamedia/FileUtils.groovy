package cavamedia

class FileUtils {

    /**
     * Used to clean up file names.  Makes the name lower case and strips problematic characters
     * @param filename: A (String) name of a file
     * @return filename: The "stripped" name
     */
    static String stripCharacters(String filename) {

        filename = filename.toLowerCase()
        filename = filename.replaceAll("[^\\d\\w\\.\\-]", "")
    }
}
