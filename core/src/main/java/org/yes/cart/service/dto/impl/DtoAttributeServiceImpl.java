package org.yes.cart.service.dto.impl;

import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttributeDTOImpl;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.dto.DtoAttributeService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Remote attribute service.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeServiceImpl
        extends AbstractDtoServiceImpl<AttributeDTO, AttributeDTOImpl, Attribute>
        implements DtoAttributeService {

    private final EtypeService etypeService;

    private final AttributeGroupService attributeGroupService;


    /**
     * Construct remote service.
     * @param attributeService {@link AttributeService}
     * @param dtoFactory {@link DtoFactory}
     * @param etypeService {@link org.yes.cart.service.domain.EtypeService}
     * @param attributeGroupService {@link AttributeGroupService}
     */
    public DtoAttributeServiceImpl(
            final AttributeService attributeService,
            final EtypeService etypeService,
            final AttributeGroupService attributeGroupService,
            final DtoFactory dtoFactory) {
        super(dtoFactory, attributeService, null);
        this.etypeService = etypeService;
        this.attributeGroupService = attributeGroupService;

    }

    /** {@inheritDoc}  */
    public AttributeDTO create(final AttributeDTO dto) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Attribute attribute = getEntityFactory().getByIface(Attribute.class);
        assembler.assembleEntity(dto, attribute,  null, dtoFactory);
        attribute.setEtype(etypeService.getById(dto.getEtypeId()));
        attribute.setAttributeGroup(attributeGroupService.getById(dto.getAttributegroupId()));
        attribute = service.create(attribute);
        return getById(attribute.getAttributeId());
    }

    /** {@inheritDoc}  */
    public AttributeDTO update(final AttributeDTO dto) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Attribute attribute = service.getById(dto.getAttributeId());
        assembler.assembleEntity(dto, attribute,  null, dtoFactory);
        attribute.setEtype(etypeService.getById(dto.getEtypeId()));
        attribute.setAttributeGroup(attributeGroupService.getById(dto.getAttributegroupId()));
        attribute = service.update(attribute);
        return getById(attribute.getAttributeId());
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findByAttributeGroupCode(final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes =  ((AttributeService)service).findByAttributeGroupCode(attributeGroupCode);
        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAvailableAttributes(
            final String attributeGroupCode,
            final List<String> assignedAttributeCodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Attribute> attributes =  ((AttributeService)service).findAvailableAttributes(
                attributeGroupCode, assignedAttributeCodes);

        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAttributesWithMultipleValues(
            final String attributeGroupCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attrs = ((AttributeService)service).findAttributesWithMultipleValues(attributeGroupCode);
        if (attrs != null) {
            final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attrs.size());
            fillDTOs(attrs, attributesDTO);
            return attributesDTO;

        }
        return null;
    }


    /** {@inheritDoc}  */
    public Class<AttributeDTO> getDtoIFace() {
        return AttributeDTO.class;
    }

    /** {@inheritDoc}  */
    public Class<AttributeDTOImpl> getDtoImpl() {
        return AttributeDTOImpl.class;
    }

    /** {@inheritDoc}  */
    public Class<Attribute> getEntityIFace() {
        return Attribute.class;
    }


}
