package com.gc.mimicry.util.model;

import java.util.concurrent.CopyOnWriteArrayList;

public class ListBasedModel<T> implements IndexedModel<T>
{

    private CopyOnWriteArrayList<IndexedModelListener<T>> listener;
    private CopyOnWriteArrayList<T> rows;

    public ListBasedModel()
    {
        rows = new CopyOnWriteArrayList<T>();
        listener = new CopyOnWriteArrayList<IndexedModelListener<T>>();
    }

    public void addIndexedModelListener(IndexedModelListener<T> l)
    {
        listener.add(l);
    }

    public void removeIndexedModelListener(IndexedModelListener<T> l)
    {
        listener.remove(l);
    }

    public int size()
    {
        return rows.size();
    }

    public T getRow(int index)
    {
        return rows.get(index);
    }

    protected void insert(T row)
    {
        rows.add(row);
        fireRowInserted(rows.size() - 1, row);
    }

    protected void insert(int index, T row)
    {
        rows.add(index, row);
        fireRowInserted(index, row);
    }

    protected T replace(int index, T newRow)
    {
        T oldRow = rows.set(index, newRow);
        fireRowUpdated(index, oldRow, newRow);
        return oldRow;
    }

    protected T remove(int index)
    {
        T oldRow = rows.remove(index);
        fireRowRemoved(index, oldRow);
        return oldRow;
    }

    protected int indexOf(T row)
    {
        return rows.indexOf(row);
    }

    protected int indexOf(T row, int index)
    {
        return rows.indexOf(row, index);
    }

    protected boolean contains(T row)
    {
        return rows.contains(row);
    }

    protected void fireRowInserted(int index, T row)
    {
        for (IndexedModelListener<T> l : listener)
        {
            l.rowInserted(this, index, row);
        }
    }

    protected void fireRowUpdated(int index, T oldRow, T newRow)
    {
        for (IndexedModelListener<T> l : listener)
        {
            l.rowUpdated(this, index, oldRow, newRow);
        }
    }

    protected void fireRowRemoved(int index, T row)
    {
        for (IndexedModelListener<T> l : listener)
        {
            l.rowRemoved(this, index, row);
        }
    }
}
