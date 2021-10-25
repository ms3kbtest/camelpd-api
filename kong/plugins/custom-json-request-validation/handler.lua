local BasePlugin = require "kong.plugins.base_plugin"

local url = require "socket.url"
local cjson = require "cjson"
local headers_cache = {}
headers_cache["Host"] = "camelpd.com"
headers_cache["Kong-Admin-Token"] = "kong"
local params_cache = {
  ssl_verify = false, -- This might be the important call
  headers = headers_cache,
}

local validaterequest = BasePlugin:extend()

validaterequest.VERSION  = "1.0.0"
validaterequest.PRIORITY = 2001


function validaterequest:init_worker()
  validaterequest.super.init_worker(self, "custom-json-request-validation")
end

-- Access the OAS here. Read the OAS and check if the request follows the schema.
function validaterequest:access(plugin_conf)

  kong.log.inspect(plugin_conf)   -- check the logs for a pretty-printed config!

  local fileName = plugin_conf.oasFileName
  local portal = plugin_conf.portalUrl

  -- TODO: Setup kong admin header stuff.

  headers_cache["Accept"] = plugin_conf.accept

  params_cache.method = "GET"
  params_cache.keepalive_timeout = 60000

  local httpc = require("resty.http").new()
  httpc:set_timeout(10000)

  

  local res, err = httpc:request_uri(portal .. fileName, params_cache)

  if not res then
    return nil, "failed request to " .. portal .. fileName .. ": " .. err
  end

  -- TODO: Parse return body to verify stuff 
  -- NOTE: use [[ some text """ ]], this way you wont have problems with nested quotes
  -- NOTE: Currently trying to figure out how to structure the table to properly have nested stuff inside and easily query 
  --       the given table. Using spaces to manipulate stuff, replacing 4 spaces to a tab, and stuff like that.
  --       \n represents end of line and \r is a carriage return(new line)
 
  kong.log(kong.request.get_path(), kong.request.get_method())

  kong.log(res.body, err)

end

return validaterequest