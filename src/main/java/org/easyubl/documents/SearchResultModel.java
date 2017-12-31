package org.easyubl.documents;

import java.util.List;

public interface SearchResultModel<T> {

    List<T> getItems();

    int getTotalResults();

}
