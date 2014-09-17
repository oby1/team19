Rails.application.routes.draw do
  root to: 'users#index'

  namespace :api, defaults: {format: :json} do
    namespace :v1 do
      resources :users do
        collection do
          delete :remove
        end
      end
    end
  end
end
