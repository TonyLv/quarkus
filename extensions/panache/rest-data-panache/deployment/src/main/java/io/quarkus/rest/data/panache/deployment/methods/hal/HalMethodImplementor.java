package io.quarkus.rest.data.panache.deployment.methods.hal;

import java.util.Collection;

import org.jboss.jandex.IndexView;

import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.rest.data.panache.deployment.RestDataResourceInfo;
import io.quarkus.rest.data.panache.deployment.methods.MethodImplementor;
import io.quarkus.rest.data.panache.deployment.methods.MethodMetadata;
import io.quarkus.rest.data.panache.deployment.properties.MethodPropertiesAccessor;
import io.quarkus.rest.data.panache.deployment.utils.ResourceName;
import io.quarkus.rest.data.panache.runtime.hal.HalCollectionWrapper;
import io.quarkus.rest.data.panache.runtime.hal.HalEntityWrapper;

abstract class HalMethodImplementor implements MethodImplementor {

    @Override
    public void implement(ClassCreator classCreator, IndexView index, MethodPropertiesAccessor propertiesAccessor,
            RestDataResourceInfo resourceInfo) {
        if (propertiesAccessor.isExposed(resourceInfo.getClassInfo(), getStandardMethodMetadata(resourceInfo))) {
            implementInternal(classCreator, index, propertiesAccessor, resourceInfo);
        }
    }

    protected abstract void implementInternal(ClassCreator classCreator, IndexView index,
            MethodPropertiesAccessor propertiesAccessor, RestDataResourceInfo resourceInfo);

    protected abstract MethodMetadata getStandardMethodMetadata(RestDataResourceInfo resourceInfo);

    protected ResultHandle wrapHalEntity(BytecodeCreator creator, ResultHandle entity) {
        return creator.newInstance(MethodDescriptor.ofConstructor(HalEntityWrapper.class, Object.class), entity);
    }

    protected ResultHandle wrapEntities(BytecodeCreator creator, ResultHandle entities, RestDataResourceInfo resourceInfo) {
        String collectionName = ResourceName.fromClass(resourceInfo.getClassInfo().simpleName());
        return creator.newInstance(
                MethodDescriptor.ofConstructor(HalCollectionWrapper.class, Collection.class, Class.class, String.class),
                entities, creator.loadClass(resourceInfo.getEntityClassName()), creator.load(collectionName));
    }
}
