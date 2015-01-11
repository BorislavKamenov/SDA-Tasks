package book;

public interface IBookIndexer {

	void buildIndex(String bookFilePath, String[] keywords, String indexFilePath);

}
