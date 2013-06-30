package com.gc.mimicry.core.model;

public interface IndexedModelListener<T>
{

    public void rowInserted(IndexedModel<T> model, int index, T row);

    public void rowUpdated(IndexedModel<T> model, int index, T oldRow, T rowNew);

    public void rowRemoved(IndexedModel<T> model, int index, T row);
}
