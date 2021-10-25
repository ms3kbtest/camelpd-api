local typedefs = require "kong.db.schema.typedefs"

-- Grab pluginname from module name
local plugin_name = ({...})[1]:match("^kong%.plugins%.([^%.]+)")

local METHODS = {
  "POST",
  "PATCH",
  "PUT"
}

local schema = {
  name = plugin_name,
  fields = {
    { consumer = typedefs.no_consumer },  
    { config = {
        type = "record",
        fields = {
          {   oasFileName = { -- could both of these just be combined to make a uri of the portal. then we just make a get request to the uri then parse it.
              type = "string",
              required = true
          }},
          {   portalUrl = {
              type = "string",
              required = true
          }},
          {   accept = {
              type = "string",
              default = "application/json"
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
      },
    },
  },
}

return schema
