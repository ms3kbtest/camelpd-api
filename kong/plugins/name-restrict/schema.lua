local typedefs = require "kong.db.schema.typedefs"

local METHODS = {
    "POST",
    "PATCH",
    "PUT"
}

return {
    name = "name-restrict",
    fields = {
        { consumer = typedefs.no_consumer },
        { config = {
            type = "record",
            fields = {
                {   restrictedStringSet = {
                    type = "array",
                    required = true,
                    elements = typedefs.header_name,
                    default = {"default","test"}
                }},
                {   restrictedFieldName = {
                    type = "string",
                    required = true,
                    default = "fieldName"
                }},
                {   methods = {
                    type = "array",
                    default = METHODS,
                    elements = {
                      type = "string",
                      one_of = METHODS,
                    }
                }},
            },
        }}
    }
}