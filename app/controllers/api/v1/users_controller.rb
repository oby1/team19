module Api::V1
  class UsersController < BaseController
    def index
      @users = User.all
    end

    def create
      @user = User.where(name: user_params[:name]).first_or_initialize
      @user.attributes = user_params

      if @user.save
        render json: {status: 'ok'}
      else
        render json: {errors: @user.errors.full_messages}, status: :bad_request
      end
    end

    def remove
      @user.find_by(name: params[:name])
      @user.destroy if @user
      render json: {status: 'ok'}
    end

    private

    def user_params
      params.require(:user).permit!
    end
  end
end
