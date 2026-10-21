/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.pool;

import java.util.ArrayList;
import java.util.List;
import xaero.common.pool.PoolUnit;

public abstract class MapPool<T extends PoolUnit> {
    private int maxSize;
    private List<T> units;

    public MapPool(int maxSize) {
        this.maxSize = maxSize;
        this.units = new ArrayList<T>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T get(Object ... args) {
        PoolUnit unit = null;
        List<T> list = this.units;
        synchronized (list) {
            if (!this.units.isEmpty()) {
                unit = (PoolUnit)this.takeFromPool();
            }
        }
        if (unit == null) {
            return this.construct(args);
        }
        unit.create(args);
        return (T)unit;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addToPool(T unit) {
        List<T> list = this.units;
        synchronized (list) {
            if (this.units.size() < this.maxSize) {
                this.units.add(unit);
                return true;
            }
        }
        return false;
    }

    private T takeFromPool() {
        return (T)((PoolUnit)this.units.remove(this.units.size() - 1));
    }

    public int size() {
        return this.units.size();
    }

    protected abstract T construct(Object ... var1);
}

