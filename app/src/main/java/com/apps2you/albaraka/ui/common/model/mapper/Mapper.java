package com.apps2you.albaraka.ui.common.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

abstract public class Mapper<TO, FROM> implements IMappable<TO, FROM> {


    public abstract TO map(FROM model);

    public FROM unmap(TO model) {
        return null;
    }

    public List<TO> map(List<FROM> models) {
        if (models == null)
            return null;
        return models.stream().map(this::map).collect(Collectors.toList());
    }

    public List<FROM> unmap(List<TO> models) {
        if (models == null)
            return null;
        return models.stream().map(this::unmap).collect(Collectors.toList());
    }
}
