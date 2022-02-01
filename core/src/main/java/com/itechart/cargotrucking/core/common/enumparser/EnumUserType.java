package com.itechart.cargotrucking.core.common.enumparser;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.LoggableUserType;

public class EnumUserType implements EnhancedUserType, DynamicParameterizedType, LoggableUserType, Serializable {

    private Class<? extends Enum> enumClass;

    private EnumValueMapper enumValueMapper;

    @Override
    public int[] sqlTypes() {
        int sqlType;
        if (enumValueMapper != null) {
            sqlType = enumValueMapper.getSqlType();
        } else {
            sqlType = Types.VARCHAR;
        }
        return new int[]{sqlType};
    }

    @Override
    public Class<? extends Enum> returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(Object o1, Object o2) {
        return o1 == o2;
    }

    @Override
    public int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return enumValueMapper.getValue(rs, names);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        enumValueMapper.setValue(st, (Enum) value, index);
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties parameters) {
        ParameterType reader = (ParameterType) parameters.get(PARAMETER_TYPE);
        if (reader != null) {
            enumClass = reader.getReturnedClass().asSubclass(Enum.class);

            Annotation[] annotations = reader.getAnnotationsMethod();
            Optional<Annotation> enumAnnotation = Stream.of(annotations)
                    .filter(Enumerated.class::isInstance).findFirst();

            if (!enumAnnotation.isPresent()) {
                throw new IllegalStateException("Enumerated persistent property in class "
                        + parameters.get(ENTITY) + " must be annotated with " + Enumerated.class);
            }
            EnumType enumType = ((Enumerated) enumAnnotation.get()).value();
            enumValueMapper = createEnumValueMapper(enumType);
        } else {
            throw new IllegalStateException("Unable to determine Enum class");
        }
    }

    private EnumValueMapper createEnumValueMapper(EnumType enumType) {
        switch (enumType) {
            case ORDINAL:
                return new OrdinalEnumValueMapper();
            case STRING:
                return new NamedEnumValueMapper();
            case POSTGRES:
                return new PGEnumValueMapper();
        }
        throw new IllegalArgumentException("Unsupported value for enum type: " + enumType);
    }

    @Override
    public String objectToSQLString(Object value) {
        return enumValueMapper.objectToSQLString((Enum) value);
    }

    @Override
    public String toXMLString(Object value) {
        return enumValueMapper.toXMLString((Enum) value);
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        return enumValueMapper.fromXMLString(xmlValue);
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) {
        if (enumValueMapper != null) {
            return enumValueMapper.toXMLString((Enum) value);
        }
        return value.toString();
    }

    private interface EnumValueMapper extends Serializable {

        int getSqlType();

        Enum getValue(ResultSet rs, String[] names) throws SQLException;

        void setValue(PreparedStatement st, Enum value, int index) throws SQLException;

        String objectToSQLString(Enum value);

        String toXMLString(Enum value);

        Enum fromXMLString(String xml);
    }

    public abstract class AbstractEnumValueMapper implements EnumValueMapper {

        protected abstract Object getJdbcValue(Enum value);

        @Override
        public void setValue(PreparedStatement st, Enum enumValue, int index) throws SQLException {
            if (enumValue != null) {
                st.setObject(index, getJdbcValue(enumValue), getSqlType());
            } else {
                st.setNull(index, getSqlType());
            }
        }
    }

    private class OrdinalEnumValueMapper extends AbstractEnumValueMapper {

        private transient Enum[] enumValues;

        @Override
        public int getSqlType() {
            return Types.INTEGER;
        }

        @Override
        public Enum getValue(ResultSet rs, String[] names) throws SQLException {
            int ordinal = rs.getInt(names[0]);
            if (rs.wasNull()) {
                return null;
            }
            return fromOrdinal(ordinal);
        }

        private Enum fromOrdinal(int ordinal) {
            Enum[] currentEnumValues = getEnumValues();
            if (ordinal < 0 || ordinal >= currentEnumValues.length) {
                throw new IllegalArgumentException(String.format("Unknown ordinal value [%s] for enum class [%s]",
                        ordinal, enumClass.getName()));
            }
            return currentEnumValues[ordinal];
        }

        private Enum[] getEnumValues() {
            if (enumValues == null) {
                enumValues = enumClass.getEnumConstants();
                if (enumValues == null) {
                    throw new HibernateException("Failed to init enum values");
                }
            }
            return enumValues;
        }

        @Override
        public String objectToSQLString(Enum value) {
            return toXMLString(value);
        }

        @Override
        public String toXMLString(Enum value) {
            return Integer.toString(value.ordinal());
        }

        @Override
        public Enum fromXMLString(String xml) {
            return fromOrdinal(Integer.parseInt(xml));
        }

        @Override
        protected Object getJdbcValue(Enum value) {
            return value.ordinal();
        }
    }

    private class NamedEnumValueMapper extends AbstractEnumValueMapper {

        @Override
        public int getSqlType() {
            return Types.VARCHAR;
        }

        @Override
        public Enum getValue(ResultSet rs, String[] names) throws SQLException {
            String value = rs.getString(names[0]);
            if (value == null) {
                return null;
            }
            return fromName(value);
        }

        private Enum fromName(String name) {
            try {
                return Enum.valueOf(enumClass, name.trim());
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException(String.format("Unknown name value [%s] for enum class [%s]",
                        name, enumClass.getName()));
            }
        }

        @Override
        public String objectToSQLString(Enum value) {
            return '\'' + value.name() + '\'';
        }

        @Override
        public String toXMLString(Enum value) {
            return value.name();
        }

        @Override
        public Enum fromXMLString(String xml) {
            return fromName(xml);
        }

        @Override
        protected Object getJdbcValue(Enum value) {
            return value.name();
        }
    }

    private class PGEnumValueMapper extends NamedEnumValueMapper {

        @Override
        public int getSqlType() {
            return Types.OTHER;
        }
    }
}
