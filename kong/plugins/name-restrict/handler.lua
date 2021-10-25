local BasePlugin = require "kong.plugins.base_plugin"

local namerestrict = BasePlugin:extend()

namerestrict.VERSION  = "1.0.0"
namerestrict.PRIORITY = 2000

function namerestrict:init_worker()
  namerestrict.super.init_worker(self, "name-restrict")
end

function namerestrict:access(config)
  namerestrict.super.access(self)

    kong.log("TEST", "LOGS")
    -- If not a configured method, exit
    -- Check if method is configured
    local valid = false

    for _, v in ipairs(config.methods) do
      if (kong.request.get_method() == v) then
        valid = true
      end
    end

    if not valid then
      return
    end

    -- get contents of body and assign to body variable
    local body, _, _ = kong.request.get_body()
    -- get the field name to restrict
    local fieldToRestrict = config.restrictedFieldName

    -- search if restricted strings are in the request body
    if body ~= nil and body[fieldToRestrict] ~= nil then
      for _, v in ipairs(config.restrictedStringSet) do
        if(string.lower(body[fieldToRestrict]) == string.lower(v)) then
          return kong.response.exit(400, [[{
            "message": "The ]] .. fieldToRestrict ..[[ entered is not allowed",
            "httpStatus": 400
          }]],
          {
            ["Content-Type"] = "application/json"
          }) -- exit when match found
        end
      end
    end
end

return namerestrict