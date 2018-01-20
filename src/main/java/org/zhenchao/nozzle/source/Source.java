package org.zhenchao.nozzle.source;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.zhenchao.nozzle.Configurable;
import static org.zhenchao.nozzle.constant.ConfigurationConstants.DEFAULT_REFERENCE_CLASS;
import static org.zhenchao.nozzle.constant.ConfigurationConstants.DEFAULT_RESOURCE_NAME;

/**
 * 数据源标识，封装目标数据源路径，以及相应的 bean
 *
 * @author zhenchao.wang 2016-12-01 14:58:03
 * @version 1.0.0
 */
public final class Source {

    private final Class<?> injectClass;

    private final String resourceName;

    public Source(Object bean) {
        Class<?> beanClass = bean.getClass();
        String resource = bean.getClass().getSimpleName().toLowerCase();

        if (bean.getClass().isAnnotationPresent(Configurable.class)) {
            Configurable configurable = bean.getClass().getAnnotation(Configurable.class);
            beanClass = DEFAULT_REFERENCE_CLASS.equals(configurable.injectClass()) ? bean.getClass() : configurable.injectClass();
            resource = DEFAULT_RESOURCE_NAME.equals(configurable.resource()) ? beanClass.getSimpleName().toLowerCase() : configurable.resource();
        }

        this.validateParams(beanClass, resource);
        this.injectClass = beanClass;
        this.resourceName = resource;
    }

    public Source(Class<?> referenceClass, String resourceName) {
        this.validateParams(referenceClass, resourceName);
        this.injectClass = referenceClass;
        this.resourceName = resourceName;
    }

    public Class<?> getInjectClass() {
        return this.injectClass;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(17, 91).append(injectClass).append(resourceName).toHashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Source other = (Source) obj;
        EqualsBuilder equals = new EqualsBuilder();
        equals.append(injectClass, other.injectClass);
        equals.append(resourceName, other.resourceName);
        return equals.isEquals();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    private void validateParams(Class<?> beanClass, String resource) {
        if (null == beanClass) {
            throw new IllegalArgumentException("missing inject class config");
        }
        if (StringUtils.isBlank(resource)) {
            throw new IllegalArgumentException("missing resource config");
        }
    }

}
