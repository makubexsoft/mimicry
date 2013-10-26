package org.mimicry.util.model;

public interface IndexedModel<T>
{

    public void addIndexedModelListener(IndexedModelListener<T> l);

    public void removeIndexedModelListener(IndexedModelListener<T> l);

    public int size();

    public T getRow(int index);
}
