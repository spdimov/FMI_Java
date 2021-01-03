package bg.sofia.uni.fmi.mjt.spellchecker;

public record Metadata(int characters, int words, int mistakes) {

    @Override
    public String toString() {
        return String.format("%d characters, %d words, %d spelling issue(s) found", characters, words, mistakes);
    }
}
