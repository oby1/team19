module Api::V1
  class BaseController < ApplicationController
    skip_before_action :verify_authenticiy_token
  end
end
